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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pt.socialfood.core.Result
import pt.socialfood.data.api.RestaurantVisitStatusApi
import pt.socialfood.data.currentTimeMillis
import pt.socialfood.data.local.dao.RestaurantVisitStatusDao
import pt.socialfood.data.local.dao.RestaurantVisitStatusRemoteKeyDao
import pt.socialfood.data.local.entity.SyncState
import pt.socialfood.data.network.extensions.toDataError
import pt.socialfood.data.paging.RestaurantVisitStatusCacheTransactionRunner
import pt.socialfood.data.paging.RestaurantVisitStatusRemoteMediator
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.domain.repository.SettingsRepository
import pt.socialfood.mapper.toRestaurantVisitStatus
import pt.socialfood.mapper.toRestaurantVisitStatusEntity

private const val MIN_SYNC_INTERVAL_MS = 5 * 60 * 1000L
private const val PAGE_SIZE = 20
private const val OPTIMISTIC_POSITION = Int.MIN_VALUE
private const val TAG = "RestaurantVisitStatusRepository"

@OptIn(ExperimentalPagingApi::class)
class RestaurantVisitStatusRepositoryImpl(
    private val restaurantVisitStatusApi: RestaurantVisitStatusApi,
    private val restaurantVisitStatusDao: RestaurantVisitStatusDao,
    private val restaurantVisitStatusRemoteKeyDao: RestaurantVisitStatusRemoteKeyDao,
    private val transactionRunner: RestaurantVisitStatusCacheTransactionRunner,
    private val settingsRepository: SettingsRepository,
) : RestaurantVisitStatusRepository {

    private val logger = Logger.withTag(TAG)
    private val syncMutex = Mutex()

    override suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit> = try {
        val entity = restaurant.toRestaurantVisitStatusEntity(
            status = status,
            recordedAt = currentTimeMillis(),
            syncState = SyncState.PENDING_ADD,
            position = OPTIMISTIC_POSITION,
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

    override fun getPagingFlow(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        remoteMediator = RestaurantVisitStatusRemoteMediator(
            status = status,
            restaurantVisitStatusApi = restaurantVisitStatusApi,
            restaurantVisitStatusDao = restaurantVisitStatusDao,
            remoteKeyDao = restaurantVisitStatusRemoteKeyDao,
            transactionRunner = transactionRunner,
        ),
        pagingSourceFactory = { restaurantVisitStatusDao.pagingSource(status.name) },
    ).flow.map { pagingData -> pagingData.map { it.toRestaurantVisitStatus() } }

    @Suppress("ReturnCount")
    override suspend fun sync(): Result<Unit> = syncMutex.withLock {
        val now = currentTimeMillis()
        val lastAttempt = settingsRepository.getLastRestaurantVisitStatusSyncAttemptAt()
        if (lastAttempt != null && now - lastAttempt < MIN_SYNC_INTERVAL_MS) {
            return@withLock Result.Success(Unit)
        }

        try {
            settingsRepository.saveLastRestaurantVisitStatusSyncAttemptAt(now)

            pushPendingMutations()

            val syncedAt = settingsRepository.getLastRestaurantVisitStatusSyncedAt()
            val changes = when (val result = safeApiCall { restaurantVisitStatusApi.sync(syncedAt) }) {
                is Result.Failure -> return@withLock result
                is Result.Success -> result.data
            }

            settingsRepository.saveLastRestaurantVisitStatusSyncedAt(changes.syncedAt)
            Result.Success(Unit)
        } catch (e: SQLiteException) {
            Result.Failure(e.toDataError())
        }
    }

    private suspend fun pushPendingMutations() {
        restaurantVisitStatusDao.getPending().forEach { entity ->
            val status = VisitStatus.valueOf(entity.status)
            when (SyncState.valueOf(entity.syncState)) {
                SyncState.PENDING_ADD -> pushPendingAdd(entity.restaurantId, status)
                SyncState.PENDING_REMOVE -> pushPendingRemove(entity.restaurantId)
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

    private suspend fun pushPendingRemove(restaurantId: String) {
        try {
            when (val result = safeApiCall { restaurantVisitStatusApi.unmark(restaurantId) }) {
                is Result.Failure ->
                    logger.w { "unmark($restaurantId) still failing (${result.error}); retried next sync." }
                is Result.Success ->
                    restaurantVisitStatusDao.deleteByRestaurantId(restaurantId)
            }
        } catch (e: SQLiteException) {
            logger.w(e) { "unmark($restaurantId) local update failed; retried next sync." }
        }
    }
}
