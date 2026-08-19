package pt.socialfood.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantStatusSyncRequest
import pt.socialfood.data.network.model.restaurantvisitstatus.RestaurantVisitStatusSyncResponse
import pt.socialfood.domain.model.VisitStatus

private const val WISH_PATH_SEGMENT = "wishlist"
private const val VISITED_PATH_SEGMENT = "visited"

internal val VisitStatus.pathSegment: String
    get() = when (this) {
        VisitStatus.WISH -> WISH_PATH_SEGMENT
        VisitStatus.VISITED -> VISITED_PATH_SEGMENT
    }

class RestaurantVisitStatusApiImpl(private val client: HttpClient) : RestaurantVisitStatusApi {

    override suspend fun mark(restaurantId: String, status: VisitStatus) {
        client.post("me/restaurants/${status.pathSegment}/$restaurantId")
    }

    override suspend fun unmark(restaurantId: String) {
        client.delete("me/restaurants/$restaurantId")
    }

    override suspend fun find(status: VisitStatus, page: Int, limit: Int): PagedResponse<RestaurantResponse> =
        client.get("me/restaurants/${status.pathSegment}") {
            parameter("page", page)
            parameter("limit", limit)
        }.body()

    override suspend fun sync(request: RestaurantStatusSyncRequest): RestaurantVisitStatusSyncResponse =
        client.post("me/restaurants/status/sync") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
