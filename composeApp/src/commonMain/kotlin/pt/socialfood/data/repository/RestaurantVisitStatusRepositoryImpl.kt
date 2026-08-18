package pt.socialfood.data.repository

import androidx.sqlite.SQLiteException
import co.touchlab.kermit.Logger
import pt.socialfood.core.Result
import pt.socialfood.data.api.RestaurantVisitStatusApi
import pt.socialfood.data.currentTimeMillis
import pt.socialfood.data.local.dao.RestaurantVisitStatusDao
import pt.socialfood.data.local.entity.SyncState
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
private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L
private const val MAX_RESTAURANT_VISITS_FETCH = 500
private const val TAG = "RestaurantVisitStatusRepository"

class RestaurantVisitStatusRepositoryImpl(
    private val restaurantVisitStatusApi: RestaurantVisitStatusApi,
    private val restaurantVisitStatusDao: RestaurantVisitStatusDao,
    private val settingsRepository: SettingsRepository,
) : RestaurantVisitStatusRepository {

    private val logger = Logger.withTag(TAG)

    override suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit> = try {
        val entity = restaurant.toRestaurantVisitStatusEntity(
            status = status,
            recordedAt = currentTimeMillis(),
            syncState = SyncState.PENDING_ADD,
        )
        restaurantVisitStatusDao.upsert(entity)

        when (val result = safeApiCall { restaurantVisitStatusApi.mark(restaurant.id, status) }) {
            is Result.Failure ->
                logger.w {
                    "mark(${restaurant.id}, $status) failed (${result.error}); " +
                        "row stays PENDING_ADD, retried by the next sync($status)."
                }

            is Result.Success<*> -> {
                restaurantVisitStatusDao.updateSyncState(restaurant.id, SyncState.SYNCED.name)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit> = try {
        restaurantVisitStatusDao.updateSyncState(restaurantId, SyncState.PENDING_REMOVE.name)

        when (val result = safeApiCall { restaurantVisitStatusApi.unmark(restaurantId) }) {
            is Result.Failure ->
                logger.w {
                    "unmark($restaurantId, $status) failed (${result.error}); " +
                        "row stays PENDING_REMOVE, retried by the next sync($status)."
                }

            is Result.Success<*> -> {
                restaurantVisitStatusDao.deleteByRestaurantId(restaurantId)
            }
        }

        Result.Success(Unit)
    } catch (e: SQLiteException) {
        Result.Failure(e.toDataError())
    }

    override suspend fun getStatus(restaurantId: String): Result<VisitStatus?> = try {
        val entity = restaurantVisitStatusDao.getByRestaurantId(restaurantId)
        val status = if (entity == null || entity.syncState == SyncState.PENDING_REMOVE.name) {
            null
        } else {
            VisitStatus.valueOf(entity.status)
        }
        Result.Success(status)
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
        val lastAttempt = settingsRepository.getLastRestaurantVisitStatusSyncAttemptAt(status)
        if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
            return Result.Success(Unit)
        }

        return try {
            settingsRepository.saveLastRestaurantVisitStatusSyncAttemptAt(status, now)

            pushPendingMutations(status)

            val syncedAt = settingsRepository.getLastRestaurantVisitStatusSyncedAt(status)
            val changes = when (val result = safeApiCall { restaurantVisitStatusApi.sync(status, since = syncedAt) }) {
                is Result.Failure -> return result
                is Result.Success -> result.data
            }

            val applyResult = applyChanges(changes)
            if (applyResult is Result.Failure) return applyResult

            settingsRepository.saveLastRestaurantVisitStatusSyncedAt(status, changes.syncedAt)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun pushPendingMutations(status: VisitStatus) {
        restaurantVisitStatusDao.getPending(status.name).forEach { entity ->
            when (SyncState.valueOf(entity.syncState)) {
                SyncState.PENDING_ADD -> pushPendingAdd(entity.restaurantId, status)
                SyncState.PENDING_REMOVE -> pushPendingRemove(entity.restaurantId, status)
                SyncState.SYNCED -> Unit
            }
        }
    }

    private suspend fun pushPendingAdd(restaurantId: String, status: VisitStatus) {
        try {
            when (val result = safeApiCall { restaurantVisitStatusApi.mark(restaurantId, status) }) {
                is Result.Failure ->
                    logger.w { "mark($restaurantId, $status) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    restaurantVisitStatusDao.updateSyncState(restaurantId, SyncState.SYNCED.name)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "mark($restaurantId, $status) local update failed; retried next sync." }
        }
    }

    private suspend fun pushPendingRemove(restaurantId: String, status: VisitStatus) {
        try {
            when (val result = safeApiCall { restaurantVisitStatusApi.unmark(restaurantId) }) {
                is Result.Failure ->
                    logger.w { "unmark($restaurantId, $status) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    restaurantVisitStatusDao.deleteByRestaurantId(restaurantId)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "unmark($restaurantId, $status) local update failed; retried next sync." }
        }
    }

    private suspend fun applyChanges(changes: RestaurantVisitStatusSyncResponse): Result<Unit> {
        if (changes.removedIds.isNotEmpty()) {
            restaurantVisitStatusDao.deleteByRestaurantIds(changes.removedIds)
        }

        val updatedByStatus = changes.updated.groupBy({ it.status }, { it.restaurantId })
        for ((entryStatus, restaurantIds) in updatedByStatus) {
            val applyResult = applyUpdatedForStatus(entryStatus, restaurantIds.toSet())
            if (applyResult is Result.Failure) return applyResult
        }

        return Result.Success(Unit)
    }

    private suspend fun applyUpdatedForStatus(status: VisitStatus, restaurantIds: Set<String>): Result<Unit> {
        val now = currentTimeMillis()
        val fetchResult = safeApiCall {
            restaurantVisitStatusApi.find(status = status, page = 1, limit = MAX_RESTAURANT_VISITS_FETCH)
        }
        val fetched = when (fetchResult) {
            is Result.Failure -> return fetchResult
            is Result.Success -> fetchResult.data
        }
        val toUpsert = fetched.items
            .filter { it.id in restaurantIds }
            .map {
                it.toRestaurant().toRestaurantVisitStatusEntity(
                    status = status,
                    recordedAt = now,
                    syncState = SyncState.SYNCED,
                )
            }
        restaurantVisitStatusDao.upsertAll(toUpsert)
        return Result.Success(Unit)
    }
}
