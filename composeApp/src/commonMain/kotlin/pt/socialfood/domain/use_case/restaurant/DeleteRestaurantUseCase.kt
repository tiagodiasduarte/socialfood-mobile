package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result

interface DeleteRestaurantUseCase {
    suspend operator fun invoke(id: String): Result<Boolean>
}
