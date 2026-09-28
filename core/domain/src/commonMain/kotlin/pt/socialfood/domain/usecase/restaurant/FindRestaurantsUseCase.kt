package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurants

interface FindRestaurantsUseCase {
    suspend operator fun invoke(page: Int, limit: Int, query: String? = null): Result<PagedRestaurants>
}
