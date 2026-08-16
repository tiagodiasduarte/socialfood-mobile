package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.RestaurantVisitStatusApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantVisitStatusSyncResponse
import pt.socialfood.domain.model.VisitStatus

class FakeRestaurantVisitStatusApi(private val shouldThrow: Boolean = false) : RestaurantVisitStatusApi {

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

    var fakeRestaurants = PagedResponse(
        items = listOf(fakeRestaurantResponse),
        page = 1,
        limit = 10,
        total = 1,
    )

    var fakeSyncResponse = RestaurantVisitStatusSyncResponse(
        addedIds = listOf(fakeRestaurantResponse.id),
        removedIds = emptyList(),
        syncedAt = "2026-08-01T10:30:00Z",
    )

    override suspend fun mark(restaurantId: String, status: VisitStatus) {
        if (shouldThrow) throw IOException("test error")
        lastMarkedRestaurantId = restaurantId
    }

    override suspend fun unmark(restaurantId: String, status: VisitStatus) {
        if (shouldThrow) throw IOException("test error")
        lastUnmarkedRestaurantId = restaurantId
    }

    override suspend fun find(status: VisitStatus, page: Int, limit: Int): PagedResponse<RestaurantResponse> {
        if (shouldThrow) throw IOException("test error")
        return fakeRestaurants
    }

    override suspend fun sync(status: VisitStatus, since: String?): RestaurantVisitStatusSyncResponse {
        if (shouldThrow) throw IOException("test error")
        return fakeSyncResponse
    }
}
