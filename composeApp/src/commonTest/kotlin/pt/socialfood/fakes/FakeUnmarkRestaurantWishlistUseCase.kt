package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.wishlist.restaurant.UnmarkRestaurantWishlistUseCase

class FakeUnmarkRestaurantWishlistUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    UnmarkRestaurantWishlistUseCase {
    var invokeCount: Int = 0
        private set
    var lastRestaurantId: String? = null
        private set

    override suspend fun invoke(restaurantId: String): Result<Unit> {
        invokeCount++
        lastRestaurantId = restaurantId
        return result
    }
}
