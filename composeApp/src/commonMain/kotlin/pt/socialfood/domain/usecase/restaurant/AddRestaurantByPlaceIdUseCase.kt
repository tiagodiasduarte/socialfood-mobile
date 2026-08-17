package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result

interface AddRestaurantByPlaceIdUseCase {
    suspend operator fun invoke(placeId: String): Result<Unit>
}
