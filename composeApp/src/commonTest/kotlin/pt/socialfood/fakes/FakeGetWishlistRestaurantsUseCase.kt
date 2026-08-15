package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.usecase.wishlist.restaurant.GetWishlistRestaurantsUseCase

class FakeGetWishlistRestaurantsUseCase(
    private val result: (page: Int) -> Result<PagedWishlistRestaurants> = {
        Result.Success(PagedWishlistRestaurants(wishlist = emptyList(), page = it, total = 0, hasMore = false))
    },
) : GetWishlistRestaurantsUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedWishlistRestaurants> {
        invokeCount++
        return result(page)
    }
}
