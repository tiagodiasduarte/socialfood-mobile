package pt.socialfood.core.network.api

import pt.socialfood.core.model.VisitStatus
import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.restaurant.RestaurantResponse
import pt.socialfood.core.network.model.restaurantvisitstatus.RestaurantVisitStatusSyncResponse

interface RestaurantVisitStatusApi {
    suspend fun mark(restaurantId: String, status: VisitStatus)

    suspend fun unmark(restaurantId: String)

    suspend fun find(status: VisitStatus, page: Int, limit: Int): PagedResponse<RestaurantResponse>

    suspend fun sync(since: String?): RestaurantVisitStatusSyncResponse
}
