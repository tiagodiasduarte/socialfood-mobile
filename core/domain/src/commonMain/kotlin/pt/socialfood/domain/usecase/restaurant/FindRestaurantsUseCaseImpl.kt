package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.repository.RestaurantsRepository

class FindRestaurantsUseCaseImpl(private val restaurantsRepository: RestaurantsRepository) : FindRestaurantsUseCase {
    override suspend fun invoke(page: Int, limit: Int, query: String?): Result<PagedRestaurants> =
        restaurantsRepository.findRestaurants(page = page, limit = limit, query = query)
}
