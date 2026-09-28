package pt.socialfood.domain.usecase.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant

interface AwaitEnrichedRestaurantByPlaceIdUseCase {
    suspend operator fun invoke(placeId: String): Result<Restaurant>
}
