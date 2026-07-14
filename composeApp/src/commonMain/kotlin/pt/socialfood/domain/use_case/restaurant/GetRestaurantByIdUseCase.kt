package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant

interface GetRestaurantByIdUseCase {
    suspend operator fun invoke(id: String): Result<Restaurant>
}
