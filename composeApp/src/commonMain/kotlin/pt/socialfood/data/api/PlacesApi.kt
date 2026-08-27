package pt.socialfood.data.api

import pt.socialfood.data.network.model.place.PlaceResponse

interface PlacesApi {
    suspend fun search(query: String): PlaceResponse
}
