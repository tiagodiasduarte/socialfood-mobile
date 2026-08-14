package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result

interface DeleteRestaurantUseCase {
    suspend operator fun invoke(id: String): Result<Boolean>
}
