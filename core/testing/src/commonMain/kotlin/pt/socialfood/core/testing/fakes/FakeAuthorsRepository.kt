package pt.socialfood.core.testing.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.AuthorsRepository
import pt.socialfood.core.model.Author
import pt.socialfood.core.model.AuthorDetail
import pt.socialfood.core.model.PagedAuthors
import pt.socialfood.core.testing.random.nextAuthorDetail
import pt.socialfood.core.testing.random.nextPagedAuthors
import kotlin.random.Random

class FakeAuthorsRepository(
    private val findAuthorsResult: Result<PagedAuthors> = Result.Success(Random.nextPagedAuthors()),
    private val findAuthorByIdResult: Result<AuthorDetail> = Result.Success(Random.nextAuthorDetail()),
    private val authorsPagingFlow: Flow<PagingData<Author>> = flowOf(PagingData.empty()),
) : AuthorsRepository {
    var findAuthorsInvokeCount: Int = 0
        private set
    var lastFindAuthorsPage: Int? = null
        private set
    var lastFindAuthorsLimit: Int? = null
        private set
    var lastFindAuthorsQuery: String? = null
        private set

    var findAuthorByIdInvokeCount: Int = 0
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
        findAuthorByIdInvokeCount++
        lastFindAuthorByIdId = id
        return findAuthorByIdResult
    }

    override fun getAuthorsPagingFlow(): Flow<PagingData<Author>> {
        getAuthorsPagingFlowInvokeCount++
        return authorsPagingFlow
    }
}
