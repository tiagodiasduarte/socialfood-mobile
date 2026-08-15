package pt.socialfood.domain.usecase.wishlist.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.WishlistRestaurantsRepository

class MarkRestaurantWishlistUseCaseImpl(private val repository: WishlistRestaurantsRepository) :
    MarkRestaurantWishlistUseCase {
    override suspend operator fun invoke(restaurant: Restaurant): Result<Unit> = repository.markWishlisted(restaurant)
}
