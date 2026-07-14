package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository

class GetRestaurantByIdUseCaseImpl(
    private val repository: RestaurantsRepository,
) : GetRestaurantByIdUseCase {
    override suspend operator fun invoke(id: String): Result<Restaurant> = repository.findById(id)
}
