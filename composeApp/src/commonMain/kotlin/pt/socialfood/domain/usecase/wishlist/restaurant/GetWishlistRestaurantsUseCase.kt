package pt.socialfood.domain.usecase.wishlist.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedWishlistRestaurants

interface GetWishlistRestaurantsUseCase {
    suspend operator fun invoke(page: Int, limit: Int): Result<PagedWishlistRestaurants>
}
