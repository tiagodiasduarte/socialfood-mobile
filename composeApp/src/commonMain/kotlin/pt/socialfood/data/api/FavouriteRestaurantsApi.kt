package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

interface FavouriteRestaurantsApi {
    suspend fun mark(restaurantId: String)

    suspend fun unmark(restaurantId: String)

    suspend fun find(page: Int, limit: Int): PagedResponse<RestaurantResponse>

    suspend fun sync(since: String?): FavouriteSyncResponse
}
