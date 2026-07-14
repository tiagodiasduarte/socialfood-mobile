package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.RestaurantApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.mapper.toRestaurant

class RestaurantsRepositoryImpl(
    private val restaurantApi: RestaurantApi
) : RestaurantsRepository {

    override suspend fun importRestaurants(): Result<Boolean> {
        return try {
            val result = restaurantApi.importRestaurants()
            Result.Success(result)

        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun delete(id: String): Result<Boolean> {
        return try {
            val restaurants = restaurantApi.delete(id)
            Result.Success(restaurants)

        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findAll(): Result<List<Restaurant>> {
        return try {
            val restaurants = restaurantApi.findAll().map { it.toRestaurant() }
            Result.Success(restaurants)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findRestaurants(page: Int, limit: Int, query: String?): Result<PagedRestaurants> {
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
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findById(id: String): Result<Restaurant> {
        return try {
            val restaurant = restaurantApi.findById(id).toRestaurant()
            Result.Success(restaurant)

        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }

    override suspend fun findByPlaceId(placeId: String): Result<Restaurant> {
        return try {
            val restaurant = restaurantApi.findByPlaceId(placeId).toRestaurant()
            Result.Success(restaurant)
        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
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

        } catch (exception: Exception) {
            Result.Error(exception.toErrorEntity())
        }
    }
}