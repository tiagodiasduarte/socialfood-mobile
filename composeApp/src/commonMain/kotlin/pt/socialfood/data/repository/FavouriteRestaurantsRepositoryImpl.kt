package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import co.touchlab.kermit.Logger
import pt.socialfood.core.Result
import pt.socialfood.data.api.FavouriteRestaurantsApi
import pt.socialfood.data.currentTimeMillis
import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.extensions.toDataError
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.mapper.toFavouriteRestaurant
import pt.socialfood.mapper.toFavouriteRestaurantEntity
import pt.socialfood.mapper.toRestaurant

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L
private const val MAX_FAVOURITES_FETCH = 500
private const val TAG = "FavouriteRestaurantsRepository"

class FavouriteRestaurantsRepositoryImpl(
    private val favouriteRestaurantsApi: FavouriteRestaurantsApi,
    private val favouriteRestaurantDao: FavouriteRestaurantDao,
    private val settingsRepository: SettingsRepository,
) : FavouriteRestaurantsRepository {

    private val logger = Logger.withTag(TAG)

    override suspend fun markFavourite(restaurant: Restaurant): Result<Unit> = try {
        val entity = restaurant.toFavouriteRestaurantEntity(
            favouritedAt = currentTimeMillis(),
            syncState = FavouriteSyncState.PENDING_ADD,
        )
        favouriteRestaurantDao.upsert(entity)

        when (val result = safeApiCall { favouriteRestaurantsApi.markFavourite(restaurant.id) }) {
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

        when (val result = safeApiCall { favouriteRestaurantsApi.unmarkFavourite(restaurantId) }) {
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

    override suspend fun getFavouritesPaged(page: Int, limit: Int): Result<PagedFavouriteRestaurants> = try {
        val offset = (page - 1) * limit
        val entities = favouriteRestaurantDao.getPaged(limit = limit, offset = offset)
        val total = favouriteRestaurantDao.countAll()
        Result.Success(
            PagedFavouriteRestaurants(
                favourites = entities.map { it.toFavouriteRestaurant() },
                page = page,
                total = total,
                hasMore = page * limit < total,
            ),
        )
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

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
            val changes = when (
                val result = safeApiCall { favouriteRestaurantsApi.syncFavouriteRestaurants(since = syncedAt) }
            ) {
                is Result.Failure -> return result
                is Result.Success -> result.data
            }

            val applyResult = applyChanges(changes)
            if (applyResult is Result.Failure) return applyResult

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
            when (val result = safeApiCall { favouriteRestaurantsApi.markFavourite(restaurantId) }) {
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
            when (val result = safeApiCall { favouriteRestaurantsApi.unmarkFavourite(restaurantId) }) {
                is Result.Failure ->
                    logger.w { "unmarkFavourite($restaurantId) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    favouriteRestaurantDao.deleteByRestaurantId(restaurantId)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "unmarkFavourite($restaurantId) local update failed; retried next sync." }
        }
    }

    private suspend fun applyChanges(changes: FavouriteSyncResponse): Result<Unit> {
        if (changes.removedIds.isNotEmpty()) {
            favouriteRestaurantDao.deleteByRestaurantIds(changes.removedIds)
        }

        if (changes.addedIds.isNotEmpty()) {
            val addedIds = changes.addedIds.toSet()
            val now = currentTimeMillis()
            val fetchResult = safeApiCall {
                favouriteRestaurantsApi.findFavouriteRestaurants(page = 1, limit = MAX_FAVOURITES_FETCH)
            }
            val allFavourites = when (fetchResult) {
                is Result.Failure -> return fetchResult
                is Result.Success -> fetchResult.data
            }
            val toUpsert = allFavourites.items
                .filter { it.id in addedIds }
                .map {
                    it.toRestaurant().toFavouriteRestaurantEntity(
                        favouritedAt = now,
                        syncState = FavouriteSyncState.SYNCED,
                    )
                }
            favouriteRestaurantDao.upsertAll(toUpsert)
        }

        return Result.Success(Unit)
    }
}
