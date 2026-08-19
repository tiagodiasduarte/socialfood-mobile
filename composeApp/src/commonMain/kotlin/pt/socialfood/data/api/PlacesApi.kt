package pt.socialfood.data.api

import pt.socialfood.data.network.NetworkConfig.API_URL
import pt.socialfood.data.network.model.place.PlaceResponse

interface PlacesApi {
    suspend fun search(query: String): PlaceResponse

    companion object {
        fun buildImageUrl(photoName: String): String = "$API_URL/places/photo?ref=$photoName"
    }
}
