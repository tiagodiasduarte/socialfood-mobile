package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import pt.socialfood.core.Result
import pt.socialfood.data.api.WishlistRestaurantsApi
import pt.socialfood.data.local.dao.WishlistRestaurantDao
import pt.socialfood.data.local.entity.WishlistSyncState
import pt.socialfood.data.network.extensions.toDataError
import pt.socialfood.data.network.model.wishlist.WishlistSyncResponse
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.domain.repository.WishlistRestaurantsRepository
import pt.socialfood.mapper.toRestaurant
import pt.socialfood.mapper.toWishlistRestaurant
import pt.socialfood.mapper.toWishlistRestaurantEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L
private const val MAX_WISHLIST_FETCH = 500

class WishlistRestaurantsRepositoryImpl(
    private val wishlistRestaurantsApi: WishlistRestaurantsApi,
    private val wishlistRestaurantDao: WishlistRestaurantDao,
    private val settingsRepository: SettingsRepository,
) : WishlistRestaurantsRepository {

    override suspend fun markWishlisted(restaurant: Restaurant): Result<Unit> = try {
        val entity = restaurant.toWishlistRestaurantEntity(
            wishlistedAt = currentTimeMillis(),
            syncState = WishlistSyncState.PENDING_ADD,
        )
        wishlistRestaurantDao.upsert(entity)

        when (val result = safeApiCall { wishlistRestaurantsApi.markWishlisted(restaurant.id) }) {
            is Result.Failure ->
                println(
                    "markWishlisted(${restaurant.id}) failed (${result.error}); " +
                        "row stays PENDING_ADD, retried by the next syncWishlist().",
                )

            is Result.Success<*> -> {
                wishlistRestaurantDao.updateSyncState(restaurant.id, WishlistSyncState.SYNCED.name)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun unmarkWishlisted(restaurantId: String): Result<Unit> = try {
        wishlistRestaurantDao.updateSyncState(restaurantId, WishlistSyncState.PENDING_REMOVE.name)

        when (val result = safeApiCall { wishlistRestaurantsApi.unmarkWishlisted(restaurantId) }) {
            is Result.Failure ->
                println(
                    "unmarkWishlisted($restaurantId) failed (${result.error}); " +
                        "row stays PENDING_REMOVE, retried by the next syncWishlist().",
                )

            is Result.Success<*> -> {
                wishlistRestaurantDao.deleteByRestaurantId(restaurantId)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun getWishlistPaged(page: Int, limit: Int): Result<PagedWishlistRestaurants> = try {
        val offset = (page - 1) * limit
        val entities = wishlistRestaurantDao.getPaged(limit = limit, offset = offset)
        val total = wishlistRestaurantDao.countAll()
        Result.Success(
            PagedWishlistRestaurants(
                wishlist = entities.map { it.toWishlistRestaurant() },
                page = page,
                total = total,
                hasMore = page * limit < total,
            ),
        )
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    @Suppress("ReturnCount")
    override suspend fun syncWishlist(): Result<Unit> {
        val now = currentTimeMillis()
        val lastAttempt = settingsRepository.getLastWishlistRestaurantsSyncAttemptAt()
        if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
            return Result.Success(Unit)
        }

        return try {
            settingsRepository.saveLastWishlistRestaurantsSyncAttemptAt(now)

            pushPendingMutations()

            val syncedAt = settingsRepository.getLastWishlistRestaurantsSyncedAt()
            val changes = when (
                val result = safeApiCall { wishlistRestaurantsApi.syncWishlistRestaurants(since = syncedAt) }
            ) {
                is Result.Failure -> return result
                is Result.Success -> result.data
            }

            val applyResult = applyChanges(changes)
            if (applyResult is Result.Failure) return applyResult

            settingsRepository.saveLastWishlistRestaurantsSyncedAt(changes.syncedAt)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun pushPendingMutations() {
        wishlistRestaurantDao.getPending().forEach { entity ->
            when (WishlistSyncState.valueOf(entity.syncState)) {
                WishlistSyncState.PENDING_ADD -> pushPendingAdd(entity.restaurantId)
                WishlistSyncState.PENDING_REMOVE -> pushPendingRemove(entity.restaurantId)
                WishlistSyncState.SYNCED -> Unit
            }
        }
    }

    private suspend fun pushPendingAdd(restaurantId: String) {
        try {
            when (val result = safeApiCall { wishlistRestaurantsApi.markWishlisted(restaurantId) }) {
                is Result.Failure ->
                    println("markWishlisted($restaurantId) still failing (${result.error}); retried next sync.")
                is Result.Success ->
                    wishlistRestaurantDao.updateSyncState(restaurantId, WishlistSyncState.SYNCED.name)
            }
        } catch (e: SQLiteException) {
            println("markWishlisted($restaurantId) local update failed ($e); retried next sync.")
        }
    }

    private suspend fun pushPendingRemove(restaurantId: String) {
        try {
            when (val result = safeApiCall { wishlistRestaurantsApi.unmarkWishlisted(restaurantId) }) {
                is Result.Failure ->
                    println("unmarkWishlisted($restaurantId) still failing (${result.error}); retried next sync.")
                is Result.Success ->
                    wishlistRestaurantDao.deleteByRestaurantId(restaurantId)
            }
        } catch (e: SQLiteException) {
            println("unmarkWishlisted($restaurantId) local update failed ($e); retried next sync.")
        }
    }

    private suspend fun applyChanges(changes: WishlistSyncResponse): Result<Unit> {
        if (changes.removedIds.isNotEmpty()) {
            wishlistRestaurantDao.deleteByRestaurantIds(changes.removedIds)
        }

        if (changes.addedIds.isNotEmpty()) {
            val addedIds = changes.addedIds.toSet()
            val now = currentTimeMillis()
            val fetchResult = safeApiCall {
                wishlistRestaurantsApi.findWishlistRestaurants(page = 1, limit = MAX_WISHLIST_FETCH)
            }
            val allWishlisted = when (fetchResult) {
                is Result.Failure -> return fetchResult
                is Result.Success -> fetchResult.data
            }
            val toUpsert = allWishlisted.items
                .filter { it.id in addedIds }
                .map {
                    it.toRestaurant().toWishlistRestaurantEntity(
                        wishlistedAt = now,
                        syncState = WishlistSyncState.SYNCED,
                    )
                }
            wishlistRestaurantDao.upsertAll(toUpsert)
        }

        return Result.Success(Unit)
    }

    @OptIn(ExperimentalTime::class)
    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
