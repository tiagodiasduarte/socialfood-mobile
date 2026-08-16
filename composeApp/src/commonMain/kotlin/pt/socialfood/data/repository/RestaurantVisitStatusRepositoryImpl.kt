package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import pt.socialfood.core.Result
import pt.socialfood.data.api.RestaurantVisitStatusApi
import pt.socialfood.data.local.dao.RestaurantVisitStatusDao
import pt.socialfood.data.local.entity.RestaurantVisitStatusSyncState
import pt.socialfood.data.network.extensions.toDataError
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantVisitStatusSyncResponse
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.mapper.toRestaurant
import pt.socialfood.mapper.toRestaurantVisitStatus
import pt.socialfood.mapper.toRestaurantVisitStatusEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L
private const val MAX_RESTAURANT_VISITS_FETCH = 500

class RestaurantVisitStatusRepositoryImpl(
    private val restaurantVisitStatusApi: RestaurantVisitStatusApi,
    private val restaurantVisitStatusDao: RestaurantVisitStatusDao,
    private val settingsRepository: SettingsRepository,
) : RestaurantVisitStatusRepository {

    override suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit> = try {
        val entity = restaurant.toRestaurantVisitStatusEntity(
            status = status,
            recordedAt = currentTimeMillis(),
            syncState = RestaurantVisitStatusSyncState.PENDING_ADD,
        )
        restaurantVisitStatusDao.upsert(entity)

        when (val result = safeApiCall { restaurantVisitStatusApi.mark(restaurant.id, status) }) {
            is Result.Failure ->
                println(
                    "mark(${restaurant.id}, $status) failed (${result.error}); " +
                        "row stays PENDING_ADD, retried by the next sync($status).",
                )

            is Result.Success<*> -> {
                restaurantVisitStatusDao.updateSyncState(restaurant.id, RestaurantVisitStatusSyncState.SYNCED.name)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit> = try {
        restaurantVisitStatusDao.updateSyncState(restaurantId, RestaurantVisitStatusSyncState.PENDING_REMOVE.name)

        when (val result = safeApiCall { restaurantVisitStatusApi.unmark(restaurantId, status) }) {
            is Result.Failure ->
                println(
                    "unmark($restaurantId, $status) failed (${result.error}); " +
                        "row stays PENDING_REMOVE, retried by the next sync($status).",
                )

            is Result.Success<*> -> {
                restaurantVisitStatusDao.deleteByRestaurantId(restaurantId)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun getPaged(status: VisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisitStatus> =
        try {
            val offset = (page - 1) * limit
            val entities = restaurantVisitStatusDao.getPaged(status = status.name, limit = limit, offset = offset)
            val total = restaurantVisitStatusDao.countAll(status.name)
            Result.Success(
                PagedRestaurantVisitStatus(
                    visits = entities.map { it.toRestaurantVisitStatus() },
                    page = page,
                    total = total,
                    hasMore = page * limit < total,
                ),
            )
        } catch (e: SQLiteException) {
            Result.Failure(e.toDataError())
        }

    @Suppress("ReturnCount")
    override suspend fun sync(status: VisitStatus): Result<Unit> {
        val now = currentTimeMillis()
        val lastAttempt = settingsRepository.getLastRestaurantVisitSyncAttemptAt(status)
        if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
            return Result.Success(Unit)
        }

        return try {
            settingsRepository.saveLastRestaurantVisitSyncAttemptAt(status, now)

            pushPendingMutations(status)

            val syncedAt = settingsRepository.getLastRestaurantVisitSyncedAt(status)
            val changes = when (val result = safeApiCall { restaurantVisitStatusApi.sync(status, since = syncedAt) }) {
                is Result.Failure -> return result
                is Result.Success -> result.data
            }

            val applyResult = applyChanges(status, changes)
            if (applyResult is Result.Failure) return applyResult

            settingsRepository.saveLastRestaurantVisitSyncedAt(status, changes.syncedAt)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun pushPendingMutations(status: VisitStatus) {
        restaurantVisitStatusDao.getPending(status.name).forEach { entity ->
            when (RestaurantVisitStatusSyncState.valueOf(entity.syncState)) {
                RestaurantVisitStatusSyncState.PENDING_ADD -> pushPendingAdd(entity.restaurantId, status)
                RestaurantVisitStatusSyncState.PENDING_REMOVE -> pushPendingRemove(entity.restaurantId, status)
                RestaurantVisitStatusSyncState.SYNCED -> Unit
            }
        }
    }

    private suspend fun pushPendingAdd(restaurantId: String, status: VisitStatus) {
        try {
            when (val result = safeApiCall { restaurantVisitStatusApi.mark(restaurantId, status) }) {
                is Result.Failure ->
                    println("mark($restaurantId, $status) still failing (${result.error}); retried next sync.")
                is Result.Success ->
                    restaurantVisitStatusDao.updateSyncState(restaurantId, RestaurantVisitStatusSyncState.SYNCED.name)
            }
        } catch (e: SQLiteException) {
            println("mark($restaurantId, $status) local update failed ($e); retried next sync.")
        }
    }

    private suspend fun pushPendingRemove(restaurantId: String, status: VisitStatus) {
        try {
            when (val result = safeApiCall { restaurantVisitStatusApi.unmark(restaurantId, status) }) {
                is Result.Failure ->
                    println("unmark($restaurantId, $status) still failing (${result.error}); retried next sync.")
                is Result.Success ->
                    restaurantVisitStatusDao.deleteByRestaurantId(restaurantId)
            }
        } catch (e: SQLiteException) {
            println("unmark($restaurantId, $status) local update failed ($e); retried next sync.")
        }
    }

    private suspend fun applyChanges(status: VisitStatus, changes: RestaurantVisitStatusSyncResponse): Result<Unit> {
        if (changes.removedIds.isNotEmpty()) {
            restaurantVisitStatusDao.deleteByRestaurantIds(changes.removedIds)
        }

        if (changes.addedIds.isNotEmpty()) {
            val addedIds = changes.addedIds.toSet()
            val now = currentTimeMillis()
            val fetchResult = safeApiCall {
                restaurantVisitStatusApi.find(status = status, page = 1, limit = MAX_RESTAURANT_VISITS_FETCH)
            }
            val allVisited = when (fetchResult) {
                is Result.Failure -> return fetchResult
                is Result.Success -> fetchResult.data
            }
            val toUpsert = allVisited.items
                .filter { it.id in addedIds }
                .map {
                    it.toRestaurant().toRestaurantVisitStatusEntity(
                        status = status,
                        recordedAt = now,
                        syncState = RestaurantVisitStatusSyncState.SYNCED,
                    )
                }
            restaurantVisitStatusDao.upsertAll(toUpsert)
        }

        return Result.Success(Unit)
    }

    @OptIn(ExperimentalTime::class)
    private fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
