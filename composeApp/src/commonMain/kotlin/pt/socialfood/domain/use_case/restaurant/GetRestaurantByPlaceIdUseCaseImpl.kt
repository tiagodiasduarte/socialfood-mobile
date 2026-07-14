package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository

class GetRestaurantByPlaceIdUseCaseImpl(
    private val repository: RestaurantsRepository,
) : GetRestaurantByPlaceIdUseCase {
    override suspend operator fun invoke(placeId: String): Result<Restaurant> = repository.findByPlaceId(placeId)
}
