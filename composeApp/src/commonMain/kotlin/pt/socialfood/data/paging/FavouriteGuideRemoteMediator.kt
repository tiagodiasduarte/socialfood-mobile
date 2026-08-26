package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.sqlite.SQLiteException
import pt.socialfood.core.Result
import pt.socialfood.data.api.FavouritesGuidesApi
import pt.socialfood.data.currentTimeMillis
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.dao.FavouriteDao
import pt.socialfood.data.local.dao.FavouriteGuideRemoteKeyDao
import pt.socialfood.data.local.entity.FavouriteGuideEntity
import pt.socialfood.data.local.entity.FavouriteGuideRemoteKeyEntity
import pt.socialfood.data.local.entity.FavouriteSyncState
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.error.toThrowable
import pt.socialfood.mapper.toFavouriteGuideEntity
import pt.socialfood.mapper.toGuide

const val FAVOURITE_GUIDES_SCOPE = "favourite_guides"

fun interface FavouriteGuideCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

fun AppDatabase.asFavouriteGuideCacheTransactionRunner(): FavouriteGuideCacheTransactionRunner =
    FavouriteGuideCacheTransactionRunner { block ->
        useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
    }

@OptIn(ExperimentalPagingApi::class)
class FavouriteGuideRemoteMediator(
    private val favouritesApi: FavouritesGuidesApi,
    private val favouriteDao: FavouriteDao,
    private val remoteKeyDao: FavouriteGuideRemoteKeyDao,
    private val transactionRunner: FavouriteGuideCacheTransactionRunner,
) : RemoteMediator<Int, FavouriteGuideEntity>() {

    @Suppress("ReturnCount")
    override suspend fun load(loadType: LoadType, state: PagingState<Int, FavouriteGuideEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = remoteKeyDao.getByScope(FAVOURITE_GUIDES_SCOPE)
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val limit = state.config.pageSize

            when (val result = safeApiCall { favouritesApi.find(page = page, limit = limit) }) {
                is Result.Failure -> MediatorResult.Error(result.error.toThrowable())
                is Result.Success<PagedResponse<GuideResponse>> -> applyResponse(result.data, loadType, page, limit)
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
        val now = currentTimeMillis()

        transactionRunner.run {
            if (loadType == LoadType.REFRESH) {
                favouriteDao.deleteAll()
                remoteKeyDao.deleteByScope(FAVOURITE_GUIDES_SCOPE)
            }
            val entities = response.items.mapIndexed { index, guideResponse ->
                guideResponse.toGuide().toFavouriteGuideEntity(
                    favouritedAt = now,
                    syncState = FavouriteSyncState.SYNCED,
                    position = (page - 1) * limit + index,
                )
            }
            favouriteDao.upsertAll(entities)
            remoteKeyDao.upsert(
                FavouriteGuideRemoteKeyEntity(
                    scope = FAVOURITE_GUIDES_SCOPE,
                    nextPage = nextPage,
                    endOfPaginationReached = endOfPaginationReached,
                ),
            )
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }
}
