package pt.socialfood.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import pt.socialfood.data.network.model.place.PlaceResponse

class PlacesApiImpl(
    private val client: HttpClient
) : PlacesApi {

    override suspend fun search(query: String): PlaceResponse =
        client.get("places/search") {
            parameter("query", query)
        }.body()
}
