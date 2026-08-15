package pt.socialfood.data.repository

import kotlinx.coroutines.delay
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.RestaurantApi
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.mapper.toRestaurant
import kotlin.time.Duration.Companion.milliseconds

class RestaurantsRepositoryImpl(private val restaurantApi: RestaurantApi) : RestaurantsRepository {

    companion object {
        internal val ENRICHMENT_POLL_INTERVAL_MS = 2_000.milliseconds
        internal const val ENRICHMENT_POLL_MAX_ATTEMPTS = 10
    }

    override suspend fun importRestaurants(): Result<Boolean> = safeApiCall { restaurantApi.importRestaurants() }

    override suspend fun delete(id: String): Result<Boolean> = safeApiCall { restaurantApi.delete(id) }

    override suspend fun findAll(): Result<List<Restaurant>> = safeApiCall {
        restaurantApi.findAll().map { it.toRestaurant() }
    }

    override suspend fun findRestaurants(page: Int, limit: Int, query: String?): Result<PagedRestaurants> =
        safeApiCall {
            val response = restaurantApi.findRestaurants(page = page, limit = limit, query = query)
            val hasMore = response.page * response.limit < response.total
            PagedRestaurants(
                restaurants = response.items.map { it.toRestaurant() },
                page = response.page,
                total = response.total,
                hasMore = hasMore,
            )
        }

    override suspend fun findById(id: String): Result<Restaurant> = safeApiCall {
        restaurantApi.findById(id).toRestaurant()
    }

    override suspend fun findByPlaceId(placeId: String): Result<Restaurant> = safeApiCall {
        restaurantApi.findByPlaceId(placeId).toRestaurant()
    }

    override suspend fun addByPlaceId(placeId: String): Result<Unit> = safeApiCall {
        restaurantApi.addByPlaceId(placeId)
    }

    override suspend fun awaitEnrichedRestaurantByPlaceId(placeId: String): Result<Restaurant> {
        repeat(ENRICHMENT_POLL_MAX_ATTEMPTS) {
            when (val result = safeApiCall { restaurantApi.findByPlaceId(placeId).toRestaurant() }) {
                is Result.Success -> return result
                is Result.Failure -> println("Restaurant not ready yet (${result.error})")
            }

            delay(ENRICHMENT_POLL_INTERVAL_MS)
        }
        return Result.Failure(DataError.Network(IOException("Restaurant enrichment timed out")))
    }

    override suspend fun update(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): Result<Restaurant> = safeApiCall {
        restaurantApi.update(
            id = id,
            name = name,
            description = description,
            country = country,
            city = city,
            address = address,
            phoneNumber = phoneNumber,
            websiteUrl = websiteUrl,
        ).toRestaurant()
    }
}
