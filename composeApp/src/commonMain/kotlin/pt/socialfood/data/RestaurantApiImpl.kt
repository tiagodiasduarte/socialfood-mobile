package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse
import pt.socialfood.data.network.model.restaurant.UpdateRestaurantRequest

class RestaurantApiImpl(
    private val client: HttpClient
) : RestaurantApi {

    override suspend fun importRestaurants(): Boolean =
        client.post("/admin/import/restaurants").body()

    override suspend fun delete(id: String): Boolean =
        client.delete("restaurants/$id").body()

    override suspend fun findAll(): List<RestaurantResponse> =
        client.get("restaurants/all").body()

    override suspend fun findRestaurants(page: Int, limit: Int, query: String?): PagedResponse<RestaurantResponse> =
        client.get("restaurants/all") {
            parameter("page", page)
            parameter("limit", limit)
            if (!query.isNullOrBlank()) parameter("search", query)
        }.body()

    override suspend fun findById(id: String): RestaurantResponse =
        client.get("restaurants/$id").body()

    override suspend fun findByPlaceId(placeId: String): RestaurantResponse =
        client.get("restaurants") {
            parameter("placeId", placeId)
        }.body()

    override suspend fun update(
        id: String,
        name: String,
        description: String?,
        country: String,
        city: String,
        address: String,
        phoneNumber: String,
        websiteUrl: String,
    ): RestaurantResponse =
        client.put("restaurants/$id") {
            contentType(ContentType.Application.Json)
            setBody(
                UpdateRestaurantRequest(
                    name = name,
                    description = description,
                    country = country,
                    city = city,
                    address = address,
                    phoneNumber = phoneNumber,
                    websiteUrl = websiteUrl,
                )
            )
        }.body()
}
