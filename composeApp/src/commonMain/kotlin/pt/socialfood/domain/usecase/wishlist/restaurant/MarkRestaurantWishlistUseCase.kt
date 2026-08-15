package pt.socialfood.domain.usecase.wishlist.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant

interface MarkRestaurantWishlistUseCase {
    suspend operator fun invoke(restaurant: Restaurant): Result<Unit>
}
