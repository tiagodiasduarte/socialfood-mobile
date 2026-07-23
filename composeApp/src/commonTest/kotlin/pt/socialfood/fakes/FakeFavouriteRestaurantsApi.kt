package pt.socialfood.fakes

import pt.socialfood.data.FavouriteRestaurantsApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteRestaurantSyncResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

class FakeFavouriteRestaurantsApi(private val shouldThrow: Boolean = false) : FavouriteRestaurantsApi {

    private val fakeRestaurantResponse = RestaurantResponse(
        id = "restaurant-id",
        name = "Restaurant Name",
        description = "Restaurant Description",
        photoNames = emptyList(),
        city = "Lisbon",
        country = "Portugal",
        countryCode = "PT",
        postalCode = "1000-000",
        phoneNumber = "+351910000000",
        address = "Rua Augusta 1",
        rating = 4.5,
        userRatingCount = 100,
        websiteUrl = null,
        location = RestaurantResponse.Location(latitude = 0.0, longitude = 0.0),
        regularOpeningHours = null,
    )

    var lastMarkedRestaurantId: String? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set

    var fakeFavouriteRestaurants = PagedResponse(
        items = listOf(fakeRestaurantResponse),
        page = 1,
        limit = 10,
        total = 1,
    )

    var fakeSyncResponse = FavouriteRestaurantSyncResponse(
        addedRestaurantIds = listOf(fakeRestaurantResponse.id),
        removedRestaurantIds = emptyList(),
        nextCheckpoint = "checkpoint-1",
    )

    override suspend fun markFavourite(restaurantId: String) {
        if (shouldThrow) throw RuntimeException("test error")
        lastMarkedRestaurantId = restaurantId
    }

    override suspend fun unmarkFavourite(restaurantId: String) {
        if (shouldThrow) throw RuntimeException("test error")
        lastUnmarkedRestaurantId = restaurantId
    }

    override suspend fun findFavouriteRestaurants(page: Int, limit: Int): PagedResponse<RestaurantResponse> {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeFavouriteRestaurants
    }

    override suspend fun syncFavouriteRestaurants(since: String?): FavouriteRestaurantSyncResponse {
        if (shouldThrow) throw RuntimeException("test error")
        return fakeSyncResponse
    }
}
