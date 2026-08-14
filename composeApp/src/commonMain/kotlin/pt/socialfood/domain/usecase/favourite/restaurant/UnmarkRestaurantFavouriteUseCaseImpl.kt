package pt.socialfood.domain.usecase.favourite.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class UnmarkRestaurantFavouriteUseCaseImpl(
    private val repository: FavouriteRestaurantsRepository,
) : UnmarkRestaurantFavouriteUseCase {
    override suspend operator fun invoke(restaurantId: String): Result<Unit> = repository.unmarkFavourite(restaurantId)
}
