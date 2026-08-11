package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository

class AwaitEnrichedRestaurantByPlaceIdUseCaseImpl(
    private val repository: RestaurantsRepository,
) : AwaitEnrichedRestaurantByPlaceIdUseCase {
    override suspend operator fun invoke(placeId: String): Result<Restaurant> =
        repository.awaitEnrichedRestaurantByPlaceId(placeId)
}
