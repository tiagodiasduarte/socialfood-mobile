package pt.socialfood.domain.usecase.wishlist.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.WishlistRestaurantsRepository

class UnmarkRestaurantWishlistUseCaseImpl(private val repository: WishlistRestaurantsRepository) :
    UnmarkRestaurantWishlistUseCase {
    override suspend fun invoke(restaurantId: String): Result<Unit> = repository.unmarkWishlisted(restaurantId)
}
