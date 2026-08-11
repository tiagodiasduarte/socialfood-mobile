package pt.socialfood.domain.usecase.favourite.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class MarkRestaurantFavouriteUseCaseImpl(
    private val repository: FavouriteRestaurantsRepository,
) : MarkRestaurantFavouriteUseCase {
    override suspend operator fun invoke(restaurant: Restaurant): Result<Unit> = repository.markFavourite(restaurant)
}
