package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.use_case.favourite.GetFavouriteGuidesUseCase

class FakeGetFavouriteGuidesUseCase(
    private val result: (page: Int) -> Result<PagedFavouriteGuides> = {
        Result.Success(PagedFavouriteGuides(favourites = emptyList(), page = it, total = 0, hasMore = false))
    },
) : GetFavouriteGuidesUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedFavouriteGuides> {
        invokeCount++
        return result(page)
    }
}
