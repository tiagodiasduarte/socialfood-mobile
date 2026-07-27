package pt.socialfood.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import pt.socialfood.data.AuthorsApi
import pt.socialfood.data.local.AppDatabase
import pt.socialfood.data.local.dao.AuthorDao
import pt.socialfood.data.local.dao.AuthorRemoteKeyDao
import pt.socialfood.data.local.entity.AuthorEntity
import pt.socialfood.data.local.entity.AuthorRemoteKeyEntity
import pt.socialfood.mapper.toAuthor
import pt.socialfood.mapper.toAuthorEntity

/**
 * Runs [block] as a single atomic write against the Authors cache.
 *
 * Mirrors [GuideCacheTransactionRunner] — a small, injectable seam rather than a raw
 * [AppDatabase] dependency, for the same two reasons documented there: `RoomDatabase.withTransaction`
 * is Android-only in Room 2.8's KMP artifact, and `AppDatabase` cannot be constructed/faked from
 * `commonTest`. Deliberately duplicated rather than shared with [GuideCacheTransactionRunner] — see
 * `authors-caching.md` for the rationale.
 */
fun interface AuthorCacheTransactionRunner {
    suspend fun run(block: suspend () -> Unit)
}

/** Production [AuthorCacheTransactionRunner] backed by a real [AppDatabase] transaction. */
fun AppDatabase.asAuthorCacheTransactionRunner(): AuthorCacheTransactionRunner =
    AuthorCacheTransactionRunner { block ->
        useWriterConnection { transactor -> transactor.immediateTransaction { block() } }
    }

/**
 * Refreshes and pages the unscoped Authors cache from the network. Registered on a
 * [androidx.paging.Pager] alongside [AuthorDao.pagingSource] so Room stays the single source of
 * truth: [load] fetches from [authorsApi] and writes into [authorDao]/[authorRemoteKeyDao], and
 * those writes invalidate the `PagingSource`, which is what actually drives the UI.
 *
 * Unlike [GuideRemoteMediator], there is no scope dimension here — this mediator only ever fetches
 * the unfiltered authors list (`query = null`).
 */
@OptIn(ExperimentalPagingApi::class)
class AuthorRemoteMediator(
    private val authorsApi: AuthorsApi,
    private val authorDao: AuthorDao,
    private val authorRemoteKeyDao: AuthorRemoteKeyDao,
    private val transactionRunner: AuthorCacheTransactionRunner,
) : RemoteMediator<Int, AuthorEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AuthorEntity>,
    ): MediatorResult {
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
            val response = authorsApi.findAuthors(page = page, limit = limit)
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
                    )
                )
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }
}
