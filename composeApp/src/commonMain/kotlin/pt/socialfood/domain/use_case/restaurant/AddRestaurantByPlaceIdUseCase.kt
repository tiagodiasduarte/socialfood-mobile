package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result

interface AddRestaurantByPlaceIdUseCase {
    suspend operator fun invoke(placeId: String): Result<Unit>
}
