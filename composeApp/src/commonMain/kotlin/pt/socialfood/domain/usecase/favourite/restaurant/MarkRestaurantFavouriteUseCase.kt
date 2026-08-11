package pt.socialfood.domain.usecase.favourite.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant

interface MarkRestaurantFavouriteUseCase {
    suspend operator fun invoke(restaurant: Restaurant): Result<Unit>
}
