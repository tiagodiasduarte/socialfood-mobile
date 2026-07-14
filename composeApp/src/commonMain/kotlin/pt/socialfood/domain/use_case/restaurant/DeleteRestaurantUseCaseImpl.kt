package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.RestaurantsRepository

class DeleteRestaurantUseCaseImpl(
    private val repository: RestaurantsRepository,
) : DeleteRestaurantUseCase {
    override suspend operator fun invoke(id: String): Result<Boolean> = repository.delete(id)
}
