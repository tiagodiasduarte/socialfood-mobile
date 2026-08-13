package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.restaurant.AddRestaurantByPlaceIdUseCase

class FakeAddRestaurantByPlaceIdUseCase(
    private val result: Result<Unit> = Result.Success(Unit),
) : AddRestaurantByPlaceIdUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(placeId: String): Result<Unit> {
        invokeCount++
        return result
    }
}
