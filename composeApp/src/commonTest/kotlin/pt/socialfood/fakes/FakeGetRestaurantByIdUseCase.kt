package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByIdUseCase

class FakeGetRestaurantByIdUseCase(
    private val result: Result<Restaurant> = Result.Error(ErrorEntity.Unknown),
) : GetRestaurantByIdUseCase {
    override suspend operator fun invoke(id: String): Result<Restaurant> = result
}
