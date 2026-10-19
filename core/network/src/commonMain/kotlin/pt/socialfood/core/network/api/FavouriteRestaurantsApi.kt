package pt.socialfood.core.network.api

import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.core.network.model.restaurant.RestaurantResponse

interface FavouriteRestaurantsApi {
    suspend fun mark(restaurantId: String)

    suspend fun unmark(restaurantId: String)

    suspend fun find(page: Int, limit: Int): PagedResponse<RestaurantResponse>

    suspend fun sync(since: String?): FavouriteSyncResponse<RestaurantResponse>
}
