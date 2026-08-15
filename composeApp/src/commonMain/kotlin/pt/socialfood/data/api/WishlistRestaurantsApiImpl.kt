package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.wishlist.WishlistSyncResponse

class WishlistRestaurantsApiImpl(private val client: HttpClient) : WishlistRestaurantsApi {

    override suspend fun markWishlisted(restaurantId: String) {
        client.post("me/restaurants/wishlist/$restaurantId")
    }

    override suspend fun unmarkWishlisted(restaurantId: String) {
        client.delete("me/restaurants/wishlist/$restaurantId")
    }

    override suspend fun findWishlistRestaurants(page: Int, limit: Int): PagedResponse<RestaurantResponse> =
        client.get("me/restaurants/wishlist") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    override suspend fun syncWishlistRestaurants(since: String?): WishlistSyncResponse =
        client.get("me/restaurants/wishlist/sync") {
            if (!since.isNullOrBlank()) parameter("since", since)
        }.body()
}
