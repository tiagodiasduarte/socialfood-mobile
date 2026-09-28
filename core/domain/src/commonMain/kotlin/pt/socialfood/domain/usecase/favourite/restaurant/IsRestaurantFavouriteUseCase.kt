package pt.socialfood.domain.usecase.favourite.restaurant

import pt.socialfood.core.Result

interface IsRestaurantFavouriteUseCase {
    suspend operator fun invoke(restaurantId: String): Result<Boolean>
}
