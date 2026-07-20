package pt.socialfood.fakes

import pt.socialfood.data.RestaurantApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

class FakeRestaurantApi(private val shouldThrow: Boolean = false) : RestaurantApi {

    private val fakeRestaurantResponse = RestaurantResponse(
        id = "restaurant-id",
        name = "Restaurant Name",
        description = "Restaurant Description",
        photoNames = listOf("photo-name-1"),
        city = "Lisbon",
        country = "Portugal",
        countryCode = "PT",
        postalCode = "1000-001",
        phoneNumber = "+351210000000",
        address = "Rua Example, 1",
        rating = 4.5,
        userRatingCount = 100,
        websiteUrl = "https://example.com",
        location = RestaurantResponse.Location(latitude = 38.7169, longitude = -9.1399),
        regularOpeningHours = listOf("Monday: 12:00 - 22:00"),
    )

    override suspend fun importRestaurants(): Boolean {
        if (shouldThrow) throw RuntimeException("test error")
        return true
    }

    override suspend fun delete(id: String): Boolean {
        if (shouldThrow) throw RuntimeException("test error")
        return true
    }

    override suspend fun findAll(): List<RestaurantResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return listOf(fakeRestaurantResponse)
    }

    override suspend fun findRestaurants(page: Int, limit: Int, query: String?): PagedResponse<RestaurantResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return PagedResponse(
            items = listOf(fakeRestaurantResponse),
            page = page,
            limit = limit,
            total = 25,
        )
    }

    override suspend fun findById(id: String): RestaurantResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeRestaurantResponse
    }

    override suspend fun findByPlaceId(placeId: String): RestaurantResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeRestaurantResponse
    }

    override suspend fun addByPlaceId(placeId: String) {
        if (shouldThrow) throw RuntimeException("test error")
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
    ): RestaurantResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeRestaurantResponse
    }
}
