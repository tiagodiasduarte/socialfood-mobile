package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteException
import pt.socialfood.core.Result
import pt.socialfood.data.api.GuidesApi
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.dao.GuideDao
import pt.socialfood.data.local.dao.GuideRemoteKeyDao
import pt.socialfood.data.local.entity.GuideEntity
import pt.socialfood.data.local.entity.GuideRemoteKeyEntity
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.error.toThrowable
import pt.socialfood.mapper.toGuide
import pt.socialfood.mapper.toGuideEntity

const val GUIDES_ALL_SCOPE = "ALL"

fun interface GuideCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

fun AppDatabase.asGuideCacheTransactionRunner(): GuideCacheTransactionRunner =
    GuideCacheTransactionRunner { block ->
        useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
    }

@OptIn(ExperimentalPagingApi::class)
class GuideRemoteMediator(
    private val scope: String,
    private val guidesApi: GuidesApi,
    private val guideDao: GuideDao,
    private val guideRemoteKeyDao: GuideRemoteKeyDao,
    private val transactionRunner: GuideCacheTransactionRunner,
) : RemoteMediator<Int, GuideEntity>() {

    @Suppress("ReturnCount")
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GuideEntity>,
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = guideRemoteKeyDao.getByScope(scope)
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val limit = state.config.pageSize

            when (
                val result = safeApiCall {
                    guidesApi.findGuides(
                        page = page,
                        limit = limit,
                        userId = scope.takeIf { it != GUIDES_ALL_SCOPE },
                    )
                }
            ) {
                is Result.Failure -> MediatorResult.Error(result.error.toThrowable())
                is Result.Success<PagedResponse<GuideResponse>> ->
                    applyResponse(result.data, loadType, page, limit)
            }
        } catch (e: SQLiteException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun applyResponse(
        response: PagedResponse<GuideResponse>,
        loadType: LoadType,
        page: Int,
        limit: Int,
    ): MediatorResult {
        val endOfPaginationReached = page * response.limit >= response.total
        val nextPage = if (endOfPaginationReached) null else page + 1

        transactionRunner.run {
            if (loadType == LoadType.REFRESH) {
                guideDao.deleteByScope(scope)
                guideRemoteKeyDao.deleteByScope(scope)
            }
            val entities = response.items.mapIndexed { index, guideResponse ->
                guideResponse.toGuide().toGuideEntity(
                    scope = scope,
                    position = (page - 1) * limit + index,
                )
            }
            guideDao.upsertAll(entities)
            guideRemoteKeyDao.upsert(
                GuideRemoteKeyEntity(
                    scope = scope,
                    nextPage = nextPage,
                    endOfPaginationReached = endOfPaginationReached,
                ),
            )
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }
}
