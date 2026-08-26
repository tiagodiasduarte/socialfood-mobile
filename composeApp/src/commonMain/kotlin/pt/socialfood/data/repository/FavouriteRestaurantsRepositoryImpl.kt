package pt.socialfood.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.sqlite.SQLiteException
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.socialfood.core.Result
import pt.socialfood.data.api.FavouriteRestaurantsApi
import pt.socialfood.data.currentTimeMillis
import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.dao.FavouriteRestaurantRemoteKeyDao
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.extensions.toDataError
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.paging.FavouriteRestaurantCacheTransactionRunner
import pt.socialfood.data.paging.FavouriteRestaurantRemoteMediator
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.mapper.toFavouriteRestaurantEntity
import pt.socialfood.mapper.toRestaurant

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L
private const val PAGE_SIZE = 20
private const val OPTIMISTIC_POSITION = Int.MIN_VALUE
private const val TAG = "FavouriteRestaurantsRepository"

@OptIn(ExperimentalPagingApi::class)
class FavouriteRestaurantsRepositoryImpl(
    private val favouriteRestaurantsApi: FavouriteRestaurantsApi,
    private val favouriteRestaurantDao: FavouriteRestaurantDao,
    private val favouriteRestaurantRemoteKeyDao: FavouriteRestaurantRemoteKeyDao,
    private val transactionRunner: FavouriteRestaurantCacheTransactionRunner,
    private val settingsRepository: SettingsRepository,
) : FavouriteRestaurantsRepository {

    private val logger = Logger.withTag(TAG)

    override suspend fun markFavourite(restaurant: Restaurant): Result<Unit> = try {
        val entity = restaurant.toFavouriteRestaurantEntity(
            favouritedAt = currentTimeMillis(),
            syncState = FavouriteSyncState.PENDING_ADD,
            position = OPTIMISTIC_POSITION,
        )
        favouriteRestaurantDao.upsert(entity)

        when (val result = safeApiCall { favouriteRestaurantsApi.mark(restaurant.id) }) {
            is Result.Failure ->
                logger.w {
                    "markFavourite(${restaurant.id}) failed (${result.error}); " +
                        "row stays PENDING_ADD, retried by the next syncFavourites()."
                }

            is Result.Success<*> -> {
                favouriteRestaurantDao.updateSyncState(restaurant.id, FavouriteSyncState.SYNCED.name)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun unmarkFavourite(restaurantId: String): Result<Unit> = try {
        favouriteRestaurantDao.updateSyncState(restaurantId, FavouriteSyncState.PENDING_REMOVE.name)

        when (val result = safeApiCall { favouriteRestaurantsApi.unmark(restaurantId) }) {
            is Result.Failure ->
                logger.w {
                    "unmarkFavourite($restaurantId) failed (${result.error}); " +
                        "row stays PENDING_REMOVE, retried by the next syncFavourites()."
                }

            is Result.Success<*> -> {
                favouriteRestaurantDao.deleteByRestaurantId(restaurantId)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override fun getFavouritesPagingFlow(): Flow<PagingData<Restaurant>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = FavouriteRestaurantRemoteMediator(
            favouritesApi = favouriteRestaurantsApi,
            favouriteDao = favouriteRestaurantDao,
            remoteKeyDao = favouriteRestaurantRemoteKeyDao,
            transactionRunner = transactionRunner,
        ),
        pagingSourceFactory = { favouriteRestaurantDao.pagingSource() },
    ).flow.map { pagingData -> pagingData.map { it.toRestaurant() } }

    override suspend fun isFavourite(restaurantId: String): Result<Boolean> = try {
        Result.Success(favouriteRestaurantDao.getByRestaurantId(restaurantId) != null)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    @Suppress("ReturnCount")
    override suspend fun syncFavourites(): Result<Unit> {
        val now = currentTimeMillis()
        val lastAttempt = settingsRepository.getLastFavouriteRestaurantsSyncAttemptAt()
        if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
            return Result.Success(Unit)
        }

        return try {
            settingsRepository.saveLastFavouriteRestaurantsSyncAttemptAt(now)

            pushPendingMutations()

            val syncedAt = settingsRepository.getLastFavouriteRestaurantsSyncedAt()
            val changes = when (val result = safeApiCall { favouriteRestaurantsApi.sync(since = syncedAt) }) {
                is Result.Failure -> return result
                is Result.Success -> result.data
            }

            applyChanges(changes)

            settingsRepository.saveLastFavouriteRestaurantsSyncedAt(changes.syncedAt)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun pushPendingMutations() {
        favouriteRestaurantDao.getPending().forEach { entity ->
            when (FavouriteSyncState.valueOf(entity.syncState)) {
                FavouriteSyncState.PENDING_ADD -> pushPendingAdd(entity.restaurantId)
                FavouriteSyncState.PENDING_REMOVE -> pushPendingRemove(entity.restaurantId)
                FavouriteSyncState.SYNCED -> Unit
            }
        }
    }

    private suspend fun pushPendingAdd(restaurantId: String) {
        try {
            when (val result = safeApiCall { favouriteRestaurantsApi.mark(restaurantId) }) {
                is Result.Failure ->
                    logger.w { "markFavourite($restaurantId) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    favouriteRestaurantDao.updateSyncState(restaurantId, FavouriteSyncState.SYNCED.name)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "markFavourite($restaurantId) local update failed; retried next sync." }
        }
    }

    private suspend fun pushPendingRemove(restaurantId: String) {
        try {
            when (val result = safeApiCall { favouriteRestaurantsApi.unmark(restaurantId) }) {
                is Result.Failure ->
                    logger.w { "unmarkFavourite($restaurantId) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    favouriteRestaurantDao.deleteByRestaurantId(restaurantId)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "unmarkFavourite($restaurantId) local update failed; retried next sync." }
        }
    }

    private suspend fun applyChanges(changes: FavouriteSyncResponse<RestaurantResponse>) {
        if (changes.removedIds.isNotEmpty()) {
            favouriteRestaurantDao.deleteByRestaurantIds(changes.removedIds)
        }

        if (changes.added.isNotEmpty()) {
            val now = currentTimeMillis()
            val toUpsert = changes.added.map {
                it.toRestaurant().toFavouriteRestaurantEntity(
                    favouritedAt = now,
                    syncState = FavouriteSyncState.SYNCED,
                    position = OPTIMISTIC_POSITION,
                )
            }
            favouriteRestaurantDao.upsertAll(toUpsert)
        }
    }
}
