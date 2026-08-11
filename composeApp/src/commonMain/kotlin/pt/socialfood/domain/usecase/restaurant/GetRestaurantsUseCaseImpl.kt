package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository

class GetRestaurantsUseCaseImpl(
    private val repository: RestaurantsRepository,
) : GetRestaurantsUseCase {
    override suspend operator fun invoke(): Result<List<Restaurant>> = repository.findAll()
}
