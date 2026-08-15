package pt.socialfood.domain.usecase.wishlist.restaurant

import pt.socialfood.core.Result

interface UnmarkRestaurantWishlistUseCase {
    suspend operator fun invoke(restaurantId: String): Result<Unit>
}
