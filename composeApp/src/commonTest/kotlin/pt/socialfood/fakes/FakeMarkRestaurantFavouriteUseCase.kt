package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.use_case.favourite.restaurant.MarkRestaurantFavouriteUseCase

class FakeMarkRestaurantFavouriteUseCase(
    private val result: Result<Unit> = Result.Success(Unit),
) : MarkRestaurantFavouriteUseCase {
    var invokeCount: Int = 0
        private set
    var lastRestaurant: Restaurant? = null
        private set

    override suspend fun invoke(restaurant: Restaurant): Result<Unit> {
        invokeCount++
        lastRestaurant = restaurant
        return result
    }
}
