package pt.socialfood.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pt.socialfood.core.Result
import pt.socialfood.data.api.AuthorsApi
import pt.socialfood.data.local.dao.AuthorDao
import pt.socialfood.data.local.dao.AuthorRemoteKeyDao
import pt.socialfood.data.paging.AuthorCacheTransactionRunner
import pt.socialfood.data.paging.AuthorRemoteMediator
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.domain.repository.AuthorsRepository
import pt.socialfood.mapper.toAuthor
import pt.socialfood.mapper.toAuthorDetail

private const val AUTHORS_PAGE_SIZE = 20

class AuthorsRepositoryImpl(
    private val authorsApi: AuthorsApi,
    private val authorDao: AuthorDao,
    private val authorRemoteKeyDao: AuthorRemoteKeyDao,
    private val transactionRunner: AuthorCacheTransactionRunner,
) : AuthorsRepository {
    override suspend fun findAuthors(page: Int, limit: Int, query: String?): Result<PagedAuthors> {
        return safeApiCall {
            val response = authorsApi.findAuthors(page = page, limit = limit, query = query)
            val hasMore = response.page * response.limit < response.total
            PagedAuthors(
                authors = response.items.map { it.toAuthor() },
                page = response.page,
                hasMore = hasMore,
            )
        }
    }

    override suspend fun findAuthorById(id: String): Result<AuthorDetail> {
        return safeApiCall { authorsApi.findAuthorById(id).toAuthorDetail() }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getAuthorsPagingFlow(): Flow<PagingData<Author>> {
        return Pager(
            config = PagingConfig(pageSize = AUTHORS_PAGE_SIZE),
            remoteMediator = AuthorRemoteMediator(
                authorsApi = authorsApi,
                authorDao = authorDao,
                authorRemoteKeyDao = authorRemoteKeyDao,
                transactionRunner = transactionRunner,
            ),
            pagingSourceFactory = { authorDao.pagingSource() },
        ).flow.map { pagingData -> pagingData.map { it.toAuthor() } }
    }
}
