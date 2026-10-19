package pt.socialfood.core.network.api

import pt.socialfood.core.network.model.place.PlaceResponse

interface PlacesApi {
    suspend fun search(query: String): PlaceResponse
}
