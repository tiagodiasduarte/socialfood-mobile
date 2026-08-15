package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.wishlist.WishlistSyncResponse

interface WishlistRestaurantsApi {
    suspend fun markWishlisted(restaurantId: String)

    suspend fun unmarkWishlisted(restaurantId: String)

    suspend fun findWishlistRestaurants(page: Int, limit: Int): PagedResponse<RestaurantResponse>

    suspend fun syncWishlistRestaurants(since: String?): WishlistSyncResponse
}
