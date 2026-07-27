package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedGuides
import pt.socialfood.domain.use_case.guide.FindGuidesUseCase

class FakeFindGuidesUseCase(
    private val result: (page: Int, userId: String?) -> Result<PagedGuides> = { page, _ ->
        Result.Success(PagedGuides(guides = emptyList(), page = page, total = 0, hasMore = false))
    },
) : FindGuidesUseCase {
    var invokeCount: Int = 0
        private set
    var lastQuery: String? = null
        private set
    var lastUserId: String? = null
        private set

    override suspend fun invoke(page: Int, limit: Int, query: String?, userId: String?): Result<PagedGuides> {
        invokeCount++
        lastQuery = query
        lastUserId = userId
        return result(page, userId)
    }
}