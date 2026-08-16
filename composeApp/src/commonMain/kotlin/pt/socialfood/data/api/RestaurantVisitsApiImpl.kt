package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.restaurantvisit.RestaurantVisitSyncResponse
import pt.socialfood.domain.model.RestaurantVisitStatus

private val RestaurantVisitStatus.pathSegment: String
    get() = when (this) {
        RestaurantVisitStatus.WISH -> "wishlist"
        RestaurantVisitStatus.VISITED -> "visited"
    }

class RestaurantVisitsApiImpl(private val client: HttpClient) : RestaurantVisitsApi {

    override suspend fun mark(restaurantId: String, status: RestaurantVisitStatus) {
        client.post("me/restaurants/${status.pathSegment}/$restaurantId")
    }

    override suspend fun unmark(restaurantId: String, status: RestaurantVisitStatus) {
        client.delete("me/restaurants/${status.pathSegment}/$restaurantId")
    }

    override suspend fun find(status: RestaurantVisitStatus, page: Int, limit: Int): PagedResponse<RestaurantResponse> =
        client.get("me/restaurants/${status.pathSegment}") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    override suspend fun sync(status: RestaurantVisitStatus, since: String?): RestaurantVisitSyncResponse =
        client.get("me/restaurants/${status.pathSegment}/sync") {
            if (!since.isNullOrBlank()) parameter("since", since)
        }.body()
}
