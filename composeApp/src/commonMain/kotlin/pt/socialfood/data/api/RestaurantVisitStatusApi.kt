package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantVisitStatusSyncResponse
import pt.socialfood.domain.model.VisitStatus

interface RestaurantVisitStatusApi {
    suspend fun mark(restaurantId: String, status: VisitStatus)

    suspend fun unmark(restaurantId: String)

    suspend fun find(status: VisitStatus, page: Int, limit: Int): PagedResponse<RestaurantResponse>

    suspend fun sync(status: VisitStatus, since: String?): RestaurantVisitStatusSyncResponse
}
