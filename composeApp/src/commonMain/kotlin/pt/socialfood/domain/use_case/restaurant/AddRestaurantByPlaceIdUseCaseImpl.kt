package pt.socialfood.domain.use_case.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.RestaurantsRepository

class AddRestaurantByPlaceIdUseCaseImpl(
    private val repository: RestaurantsRepository,
) : AddRestaurantByPlaceIdUseCase {
    override suspend operator fun invoke(placeId: String): Result<Unit> = repository.addByPlaceId(placeId)
}
