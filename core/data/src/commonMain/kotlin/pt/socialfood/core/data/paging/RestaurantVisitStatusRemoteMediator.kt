package pt.socialfood.core.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteException
import pt.socialfood.core.common.Result
import pt.socialfood.core.common.error.toThrowable
import pt.socialfood.core.data.currentTimeMillis
import pt.socialfood.core.data.mapper.toRestaurant
import pt.socialfood.core.data.mapper.toRestaurantVisitStatusEntity
import pt.socialfood.core.database.AppDatabase
import pt.socialfood.core.database.dao.RestaurantVisitStatusDao
import pt.socialfood.core.database.dao.RestaurantVisitStatusRemoteKeyDao
import pt.socialfood.core.database.entity.RestaurantVisitStatusEntity
import pt.socialfood.core.database.entity.RestaurantVisitStatusRemoteKeyEntity
import pt.socialfood.core.database.entity.SyncState
import pt.socialfood.core.model.VisitStatus
import pt.socialfood.core.network.api.RestaurantVisitStatusApi
import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.restaurant.RestaurantResponse
import pt.socialfood.core.network.safeApiCall

fun interface RestaurantVisitStatusCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

fun AppDatabase.asRestaurantVisitStatusCacheTransactionRunner(): RestaurantVisitStatusCacheTransactionRunner =
    RestaurantVisitStatusCacheTransactionRunner { block ->
        useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
    }

@OptIn(ExperimentalPagingApi::class)
class RestaurantVisitStatusRemoteMediator(
    private val status: VisitStatus,
    private val restaurantVisitStatusApi: RestaurantVisitStatusApi,
    private val restaurantVisitStatusDao: RestaurantVisitStatusDao,
    private val remoteKeyDao: RestaurantVisitStatusRemoteKeyDao,
    private val transactionRunner: RestaurantVisitStatusCacheTransactionRunner,
) : RemoteMediator<Int, RestaurantVisitStatusEntity>() {

    private val scope = status.name

    @Suppress("ReturnCount")
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, RestaurantVisitStatusEntity>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getByScope(scope)
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val limit = state.config.pageSize

            when (
                val result = safeApiCall {
                    restaurantVisitStatusApi.find(status = status, page = page, limit = limit)
                }
            ) {
                is Result.Failure -> MediatorResult.Error(result.error.toThrowable())
                is Result.Success<PagedResponse<RestaurantResponse>> ->
                    applyResponse(result.data, loadType, page, limit)
            }
        } catch (e: SQLiteException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun applyResponse(
        response: PagedResponse<RestaurantResponse>,
        loadType: LoadType,
        page: Int,
        limit: Int,
    ): MediatorResult {
        val endOfPaginationReached = page * response.limit >= response.total
        val nextPage = if (endOfPaginationReached) null else page + 1
        val now = currentTimeMillis()

        transactionRunner.run {
            if (loadType == LoadType.REFRESH) {
                restaurantVisitStatusDao.deleteByStatus(scope)
                remoteKeyDao.deleteByScope(scope)
            }
            val entities = response.items.mapIndexed { index, restaurantResponse ->
                restaurantResponse.toRestaurant().toRestaurantVisitStatusEntity(
                    status = status,
                    recordedAt = now,
                    syncState = SyncState.SYNCED,
                    position = (page - 1) * limit + index,
                )
            }
            restaurantVisitStatusDao.upsertAll(entities)
            remoteKeyDao.upsert(
                RestaurantVisitStatusRemoteKeyEntity(
                    scope = scope,
                    nextPage = nextPage,
                    endOfPaginationReached = endOfPaginationReached,
                ),
            )
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }
}
