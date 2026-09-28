package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.domain.usecase.author.FindAuthorsUseCase

class FakeFindAuthorsUseCase(
    private val result: (page: Int, query: String?) -> Result<PagedAuthors> = { page, _ ->
        Result.Success(PagedAuthors(authors = emptyList(), page = page, hasMore = false))
    },
) : FindAuthorsUseCase {
    var invokeCount: Int = 0
        private set
    var lastQuery: String? = null
        private set

    override suspend fun invoke(page: Int, limit: Int, query: String?): Result<PagedAuthors> {
        invokeCount++
        lastQuery = query
        return result(page, query)
    }
}
