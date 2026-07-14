package pt.socialfood.data

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

interface RestaurantApi {
    suspend fun importRestaurants(): Boolean

    suspend fun delete(id: String): Boolean

    suspend fun findAll(): List<RestaurantResponse>

    suspend fun findRestaurants(page: Int, limit: Int, query: String? = null): PagedResponse<RestaurantResponse>

    suspend fun findById(id: String): RestaurantResponse

    suspend fun findByPlaceId(placeId: String): RestaurantResponse

    suspend fun update(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): RestaurantResponse
}
