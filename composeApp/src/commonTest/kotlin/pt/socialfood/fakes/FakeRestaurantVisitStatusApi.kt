package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.RestaurantVisitStatusApi
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantStatusSyncRequest
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantVisitStatusSyncResponse
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.random.nextRestaurantResponse
import kotlin.random.Random

class FakeRestaurantVisitStatusApi(private val shouldThrow: Boolean = false) : RestaurantVisitStatusApi {

    private val fakeRestaurantResponse = Random.nextRestaurantResponse()

    var lastMarkedRestaurantId: String? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set

    var lastSyncRequest: RestaurantStatusSyncRequest? = null
        private set

    var fakeRestaurants = PagedResponse(
        items = listOf(fakeRestaurantResponse),
        page = 1,
        limit = 10,
        total = 1,
    )

    var fakeSyncResponse = RestaurantVisitStatusSyncResponse(
        updated = emptyList(),
        removedIds = emptyList(),
        syncedAt = "2026-08-01T10:30:00Z",
    )

    override suspend fun mark(restaurantId: String, status: VisitStatus) {
        if (shouldThrow) throw IOException("test error")
        lastMarkedRestaurantId = restaurantId
    }

    override suspend fun unmark(restaurantId: String) {
        if (shouldThrow) throw IOException("test error")
        lastUnmarkedRestaurantId = restaurantId
    }

    override suspend fun find(status: VisitStatus, page: Int, limit: Int): PagedResponse<RestaurantResponse> {
        if (shouldThrow) throw IOException("test error")
        return fakeRestaurants
    }

    override suspend fun sync(request: RestaurantStatusSyncRequest): RestaurantVisitStatusSyncResponse {
        if (shouldThrow) throw IOException("test error")
        lastSyncRequest = request
        return fakeSyncResponse
    }
}
