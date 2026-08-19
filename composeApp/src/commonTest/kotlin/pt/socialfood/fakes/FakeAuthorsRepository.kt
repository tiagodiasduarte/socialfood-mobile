package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.domain.repository.AuthorsRepository
import pt.socialfood.random.nextAuthorDetail
import pt.socialfood.random.nextPagedAuthors
import kotlin.random.Random

class FakeAuthorsRepository(
    private val findAuthorsResult: Result<PagedAuthors> = Result.Success(Random.nextPagedAuthors()),
    private val findAuthorByIdResult: Result<AuthorDetail> = Result.Success(Random.nextAuthorDetail()),
    private val authorsPagingFlow: Flow<PagingData<Author>> = emptyFlow(),
) : AuthorsRepository {
    var findAuthorsInvokeCount: Int = 0
        private set
    var lastFindAuthorsPage: Int? = null
        private set
    var lastFindAuthorsLimit: Int? = null
        private set
    var lastFindAuthorsQuery: String? = null
        private set

    var lastFindAuthorByIdId: String? = null
        private set

    var getAuthorsPagingFlowInvokeCount: Int = 0
        private set

    override suspend fun findAuthors(page: Int, limit: Int, query: String?): Result<PagedAuthors> {
        findAuthorsInvokeCount++
        lastFindAuthorsPage = page
        lastFindAuthorsLimit = limit
        lastFindAuthorsQuery = query
        return findAuthorsResult
    }

    override suspend fun findAuthorById(id: String): Result<AuthorDetail> {
        lastFindAuthorByIdId = id
        return findAuthorByIdResult
    }

    override fun getAuthorsPagingFlow(): Flow<PagingData<Author>> {
        getAuthorsPagingFlowInvokeCount++
        return authorsPagingFlow
    }
}
