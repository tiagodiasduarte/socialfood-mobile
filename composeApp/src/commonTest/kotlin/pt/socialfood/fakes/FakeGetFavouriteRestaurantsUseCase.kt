package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.use_case.favourite.restaurant.GetFavouriteRestaurantsUseCase

class FakeGetFavouriteRestaurantsUseCase(
    private val result: (page: Int) -> Result<PagedFavouriteRestaurants> = {
        Result.Success(PagedFavouriteRestaurants(favourites = emptyList(), page = it, total = 0, hasMore = false))
    },
) : GetFavouriteRestaurantsUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedFavouriteRestaurants> {
        invokeCount++
        return result(page)
    }
}
