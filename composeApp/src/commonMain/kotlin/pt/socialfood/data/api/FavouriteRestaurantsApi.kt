package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

interface FavouriteRestaurantsApi {
    suspend fun markFavourite(restaurantId: String)

    suspend fun unmarkFavourite(restaurantId: String)

    suspend fun findFavouriteRestaurants(page: Int, limit: Int): PagedResponse<RestaurantResponse>

    suspend fun syncFavouriteRestaurants(since: String?): FavouriteSyncResponse
}
