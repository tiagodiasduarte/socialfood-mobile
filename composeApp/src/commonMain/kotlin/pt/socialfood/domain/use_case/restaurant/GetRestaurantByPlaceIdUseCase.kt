package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant

interface GetRestaurantByPlaceIdUseCase {
    suspend operator fun invoke(placeId: String): Result<Restaurant>
}
