package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.favourite.restaurant.IsRestaurantFavouriteUseCase

class FakeIsRestaurantFavouriteUseCase(
    private val result: Result<Boolean> = Result.Success(false),
) : IsRestaurantFavouriteUseCase {
    override suspend fun invoke(restaurantId: String): Result<Boolean> = result
}
