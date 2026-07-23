package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteRestaurantSyncResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

class FavouriteRestaurantsApiImpl(
    private val client: HttpClient
) : FavouriteRestaurantsApi {

    override suspend fun markFavourite(restaurantId: String) {
        client.post("restaurants/$restaurantId/favourite")
    }

    override suspend fun unmarkFavourite(restaurantId: String) {
        client.delete("restaurants/$restaurantId/favourite")
    }

    override suspend fun findFavouriteRestaurants(page: Int, limit: Int): PagedResponse<RestaurantResponse> =
        client.get("me/favourites/restaurants") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    override suspend fun syncFavouriteRestaurants(since: String?): FavouriteRestaurantSyncResponse =
        client.get("me/favourites/restaurants/sync") {
            if (!since.isNullOrBlank()) parameter("since", since)
        }.body()
}
