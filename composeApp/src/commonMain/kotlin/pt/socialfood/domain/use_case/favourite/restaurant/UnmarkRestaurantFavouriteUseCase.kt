package pt.socialfood.domain.use_case.favourite.restaurant

import pt.socialfood.core.Result

interface UnmarkRestaurantFavouriteUseCase {
    suspend operator fun invoke(restaurantId: String): Result<Unit>
}
