package pt.socialfood.core.domain.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.PagedRestaurants
import pt.socialfood.core.model.Restaurant

interface RestaurantsRepository {

    suspend fun importRestaurants(): Result<Boolean>
    suspend fun delete(id: String): Result<Boolean>
    suspend fun findAll(): Result<List<Restaurant>>
    suspend fun findRestaurants(page: Int, limit: Int, query: String? = null): Result<PagedRestaurants>
    suspend fun findById(id: String): Result<Restaurant>
    suspend fun findByPlaceId(placeId: String): Result<Restaurant>
    suspend fun addByPlaceId(placeId: String): Result<Unit>

    suspend fun awaitEnrichedRestaurantByPlaceId(placeId: String): Result<Restaurant>
    suspend fun update(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): Result<Restaurant>
}
