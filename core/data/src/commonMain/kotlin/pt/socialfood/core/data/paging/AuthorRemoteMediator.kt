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
import pt.socialfood.core.data.mapper.toAuthor
import pt.socialfood.core.data.mapper.toAuthorEntity
import pt.socialfood.core.database.AppDatabase
import pt.socialfood.core.database.dao.AuthorDao
import pt.socialfood.core.database.dao.AuthorRemoteKeyDao
import pt.socialfood.core.database.entity.AuthorEntity
import pt.socialfood.core.database.entity.AuthorRemoteKeyEntity
import pt.socialfood.core.network.api.AuthorsApi
import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.author.AuthorResponse
import pt.socialfood.core.network.safeApiCall

fun interface AuthorCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

fun AppDatabase.asAuthorCacheTransactionRunner(): AuthorCacheTransactionRunner = AuthorCacheTransactionRunner { block ->
    useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
}

@OptIn(ExperimentalPagingApi::class)
class AuthorRemoteMediator(
    private val authorsApi: AuthorsApi,
    private val authorDao: AuthorDao,
    private val authorRemoteKeyDao: AuthorRemoteKeyDao,
    private val transactionRunner: AuthorCacheTransactionRunner,
) : RemoteMediator<Int, AuthorEntity>() {

    @Suppress("ReturnCount")
    override suspend fun load(loadType: LoadType, state: PagingState<Int, AuthorEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = authorRemoteKeyDao.get()
                    remoteKey?.nextPage
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val limit = state.config.pageSize

            when (val result = safeApiCall { authorsApi.findAuthors(page = page, limit = limit) }) {
                is Result.Failure -> MediatorResult.Error(result.error.toThrowable())
                is Result.Success<PagedResponse<AuthorResponse>> ->
                    applyResponse(result.data, loadType, page, limit)
            }
        } catch (e: SQLiteException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun applyResponse(
        response: PagedResponse<AuthorResponse>,
        loadType: LoadType,
        page: Int,
        limit: Int,
    ): MediatorResult {
        val endOfPaginationReached = page * response.limit >= response.total
        val nextPage = if (endOfPaginationReached) null else page + 1

        transactionRunner.run {
            if (loadType == LoadType.REFRESH) {
                authorDao.deleteAll()
                authorRemoteKeyDao.deleteAll()
            }
            val entities = response.items.mapIndexed { index, authorResponse ->
                authorResponse.toAuthor().toAuthorEntity(
                    position = (page - 1) * limit + index,
                )
            }
            authorDao.upsertAll(entities)
            authorRemoteKeyDao.upsert(
                AuthorRemoteKeyEntity(
                    nextPage = nextPage,
                    endOfPaginationReached = endOfPaginationReached,
                ),
            )
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }
}
