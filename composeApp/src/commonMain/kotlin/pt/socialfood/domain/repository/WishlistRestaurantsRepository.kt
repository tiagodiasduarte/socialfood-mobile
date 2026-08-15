package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.model.Restaurant

interface WishlistRestaurantsRepository {
    suspend fun markWishlisted(restaurant: Restaurant): Result<Unit>

    suspend fun unmarkWishlisted(restaurantId: String): Result<Unit>

    suspend fun getWishlistPaged(page: Int, limit: Int): Result<PagedWishlistRestaurants>

    suspend fun syncWishlist(): Result<Unit>
}
