package pt.socialfood.domain.use_case.favourite.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class IsRestaurantFavouriteUseCaseImpl(
    private val repository: FavouriteRestaurantsRepository,
) : IsRestaurantFavouriteUseCase {
    override suspend operator fun invoke(restaurantId: String): Result<Boolean> = repository.isFavourite(restaurantId)
}
