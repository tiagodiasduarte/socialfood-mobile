package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.FavouriteRestaurantsApi
import pt.socialfood.data.local.dao.FavouriteRestaurantDao
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.mapper.toFavouriteRestaurant
import pt.socialfood.mapper.toFavouriteRestaurantEntity
import pt.socialfood.mapper.toRestaurant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L

// The number of favourites a user can have is bounded, so one page covers the whole set —
// no need for true incremental pagination when hydrating newly-added favourites.
private const val MAX_FAVOURITES_FETCH = 500

class FavouriteRestaurantsRepositoryImpl(
    private val favouriteRestaurantsApi: FavouriteRestaurantsApi,
    private val favouriteRestaurantDao: FavouriteRestaurantDao,
    private val settingsRepository: SettingsRepository,
) : FavouriteRestaurantsRepository {
    override suspend fun markFavourite(restaurant: Restaurant): Result<Unit> =
        try {
            val entity =
                restaurant.toFavouriteRestaurantEntity(
                    favouritedAt = currentTimeMillis(),
                    syncState = FavouriteSyncState.PENDING_ADD,
                )
            favouriteRestaurantDao.upsert(entity)

            try {
                favouriteRestaurantsApi.markFavourite(restaurant.id)
                favouriteRestaurantDao.updateSyncState(restaurant.id, FavouriteSyncState.SYNCED.name)
            } catch (_: Exception) {
                // Network failed — row stays PENDING_ADD, retried by the next syncFavourites().
            }

            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }

    override suspend fun unmarkFavourite(restaurantId: String): Result<Unit> =
        try {
            favouriteRestaurantDao.updateSyncState(restaurantId, FavouriteSyncState.PENDING_REMOVE.name)

            try {
                favouriteRestaurantsApi.unmarkFavourite(restaurantId)
                favouriteRestaurantDao.deleteByRestaurantId(restaurantId)
            } catch (_: Exception) {
                // Network failed — row stays PENDING_REMOVE, retried by the next syncFavourites().
            }

            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }

    override suspend fun getFavouritesPaged(
        page: Int,
        limit: Int,
    ): Result<PagedFavouriteRestaurants> =
        try {
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
            Result.Error(e.toErrorEntity())
        }

    override suspend fun isFavourite(restaurantId: String): Result<Boolean> =
        try {
            Result.Success(favouriteRestaurantDao.getByRestaurantId(restaurantId) != null)
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }

    override suspend fun syncFavourites(): Result<Unit> {
        return try {
            val now = currentTimeMillis()
            val lastAttempt = settingsRepository.getLastFavouriteRestaurantsSyncAttemptAt()
            if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
                return Result.Success(Unit)
            }
            settingsRepository.saveLastFavouriteRestaurantsSyncAttemptAt(now)

            pushPendingMutations()

            val checkpoint = settingsRepository.getFavouriteRestaurantsSyncCheckpoint()
            val changes = favouriteRestaurantsApi.syncFavouriteRestaurants(since = checkpoint)

            applyChanges(changes)

            settingsRepository.saveFavouriteRestaurantsSyncCheckpoint(changes.nextCheckpoint)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (e: SQLiteException) {
            Result.Error(e.toErrorEntity())
        }
    }

    private suspend fun pushPendingMutations() {
        favouriteRestaurantDao.getPending().forEach { entity ->
            when (FavouriteSyncState.valueOf(entity.syncState)) {
                FavouriteSyncState.PENDING_ADD ->
                    try {
                        favouriteRestaurantsApi.markFavourite(entity.restaurantId)
                        favouriteRestaurantDao.updateSyncState(entity.restaurantId, FavouriteSyncState.SYNCED.name)
                    } catch (_: Exception) {
                        // Still offline/failing — retried next sync.
                    }

                FavouriteSyncState.PENDING_REMOVE ->
                    try {
                        favouriteRestaurantsApi.unmarkFavourite(entity.restaurantId)
                        favouriteRestaurantDao.deleteByRestaurantId(entity.restaurantId)
                    } catch (_: Exception) {
                        // Still offline/failing — retried next sync.
                    }

                FavouriteSyncState.SYNCED -> Unit
            }
        }
    }

    private suspend fun applyChanges(changes: FavouriteSyncResponse) {
        if (changes.removedIds.isNotEmpty()) {
            favouriteRestaurantDao.deleteByRestaurantIds(changes.removedIds)
        }

        if (changes.addedIds.isNotEmpty()) {
            val addedIds = changes.addedIds.toSet()
            val now = currentTimeMillis()
            val allFavourites = favouriteRestaurantsApi.findFavouriteRestaurants(page = 1, limit = MAX_FAVOURITES_FETCH)
            val toUpsert =
                allFavourites.items
                    .filter { it.id in addedIds }
                    .map {
                        it.toRestaurant().toFavouriteRestaurantEntity(
                            favouritedAt = now,
                            syncState = FavouriteSyncState.SYNCED,
                        )
                    }
            favouriteRestaurantDao.upsertAll(toUpsert)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
