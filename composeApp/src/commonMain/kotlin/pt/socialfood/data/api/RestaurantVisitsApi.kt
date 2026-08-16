package pt.socialfood.data.api

import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.restaurantvisit.RestaurantVisitSyncResponse
import pt.socialfood.domain.model.RestaurantVisitStatus

interface RestaurantVisitsApi {
    suspend fun mark(restaurantId: String, status: RestaurantVisitStatus)

    suspend fun unmark(restaurantId: String, status: RestaurantVisitStatus)

    suspend fun find(status: RestaurantVisitStatus, page: Int, limit: Int): PagedResponse<RestaurantResponse>

    suspend fun sync(status: RestaurantVisitStatus, since: String?): RestaurantVisitSyncResponse
}
