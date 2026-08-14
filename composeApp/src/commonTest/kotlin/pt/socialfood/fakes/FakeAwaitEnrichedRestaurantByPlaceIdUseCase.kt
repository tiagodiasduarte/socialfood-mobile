package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.usecase.restaurant.AwaitEnrichedRestaurantByPlaceIdUseCase

class FakeAwaitEnrichedRestaurantByPlaceIdUseCase(
    private val result: Result<Restaurant>,
) : AwaitEnrichedRestaurantByPlaceIdUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(placeId: String): Result<Restaurant> {
        invokeCount++
        return result
    }
}
