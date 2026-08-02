package pt.socialfood.data.repository

import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.delay
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.RestaurantApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.mapper.toRestaurant
import kotlin.time.Duration.Companion.milliseconds

class RestaurantsRepositoryImpl(
    private val restaurantApi: RestaurantApi
) : RestaurantsRepository {

    companion object {
        internal val ENRICHMENT_POLL_INTERVAL_MS = 2_000.milliseconds
        internal const val ENRICHMENT_POLL_MAX_ATTEMPTS = 10
    }

    override suspend fun importRestaurants(): Result<Boolean> {
        return try {
            val result = restaurantApi.importRestaurants()
            Result.Success(result)

        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun delete(id: String): Result<Boolean> {
        return try {
            val restaurants = restaurantApi.delete(id)
            Result.Success(restaurants)

        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun findAll(): Result<List<Restaurant>> {
        return try {
            val restaurants = restaurantApi.findAll().map { it.toRestaurant() }
            Result.Success(restaurants)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun findRestaurants(
        page: Int,
        limit: Int,
        query: String?
    ): Result<PagedRestaurants> {
        return try {
            val response = restaurantApi.findRestaurants(page = page, limit = limit, query = query)
            val hasMore = response.page * response.limit < response.total
            Result.Success(
                PagedRestaurants(
                    restaurants = response.items.map { it.toRestaurant() },
                    page = response.page,
                    total = response.total,
                    hasMore = hasMore,
                )
            )
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun findById(id: String): Result<Restaurant> {
        return try {
            val restaurant = restaurantApi.findById(id).toRestaurant()
            Result.Success(restaurant)

        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun findByPlaceId(placeId: String): Result<Restaurant> {
        return try {
            val restaurant = restaurantApi.findByPlaceId(placeId).toRestaurant()
            Result.Success(restaurant)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun addByPlaceId(placeId: String): Result<Unit> {
        return try {
            restaurantApi.addByPlaceId(placeId)
            Result.Success(Unit)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }

    override suspend fun awaitEnrichedRestaurantByPlaceId(placeId: String): Result<Restaurant> {
        repeat(ENRICHMENT_POLL_MAX_ATTEMPTS) {
            try {
                val response = restaurantApi.findByPlaceId(placeId)
                return Result.Success(response.toRestaurant())
            } catch (e: IOException) {
                println("Restaurant not ready yet ($e)")
            } catch (e: ResponseException) {
                println("Restaurant not ready yet ($e)")
            } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
                println("Restaurant not ready yet ($e)")
            }

            delay(ENRICHMENT_POLL_INTERVAL_MS)
        }
        return Result.Error(ErrorEntity.Network.TIMEOUT)
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
    ): Result<Restaurant> {
        return try {
            val restaurant = restaurantApi.update(
                id = id,
                name = name,
                description = description,
                country = country,
                city = city,
                address = address,
                phoneNumber = phoneNumber,
                websiteUrl = websiteUrl,
            ).toRestaurant()
            Result.Success(restaurant)

        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            Result.Error(e.toErrorEntity())
        }
    }
}
