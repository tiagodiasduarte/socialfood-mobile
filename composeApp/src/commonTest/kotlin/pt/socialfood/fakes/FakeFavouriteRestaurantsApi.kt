package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.FavouriteRestaurantsApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.favourite.FavouriteSyncResponse
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

    var findFavouriteRestaurantsCallCount: Int = 0
        private set

    var lastFindFavouriteRestaurantsPage: Int? = null
        private set

    var fakeFavouriteRestaurants = PagedResponse(
        items = listOf(fakeRestaurantResponse),
        page = 1,
        limit = 10,
        total = 1,
    )

    var fakeSyncResponse = FavouriteSyncResponse(
        added = listOf(fakeRestaurantResponse),
        removedIds = emptyList(),
        syncedAt = "2026-08-01T10:30:00Z",
    )

    override suspend fun mark(restaurantId: String) {
        if (shouldThrow) throw IOException("test error")
        lastMarkedRestaurantId = restaurantId
    }

    override suspend fun unmark(restaurantId: String) {
        if (shouldThrow) throw IOException("test error")
        lastUnmarkedRestaurantId = restaurantId
    }

    override suspend fun find(page: Int, limit: Int): PagedResponse<RestaurantResponse> {
        if (shouldThrow) throw IOException("test error")
        findFavouriteRestaurantsCallCount++
        lastFindFavouriteRestaurantsPage = page
        return fakeFavouriteRestaurants
    }

    override suspend fun sync(since: String?): FavouriteSyncResponse<RestaurantResponse> {
        if (shouldThrow) throw IOException("test error")
        return fakeSyncResponse
    }
}
