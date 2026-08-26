package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

private const val FAVOURITE_RESTAURANTS_PATH = "me/favourites/restaurants"

class FavouriteRestaurantsApiImpl(private val client: HttpClient) : FavouriteRestaurantsApi {

    override suspend fun mark(restaurantId: String) {
        client.post("$FAVOURITE_RESTAURANTS_PATH/$restaurantId")
    }

    override suspend fun unmark(restaurantId: String) {
        client.delete("$FAVOURITE_RESTAURANTS_PATH/$restaurantId")
    }

    override suspend fun find(page: Int, limit: Int): PagedResponse<RestaurantResponse> =
        client.get(FAVOURITE_RESTAURANTS_PATH) {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    override suspend fun sync(since: String?): FavouriteSyncResponse = client.get("$FAVOURITE_RESTAURANTS_PATH/sync") {
        if (!since.isNullOrBlank()) parameter("since", since)
    }.body()
}
