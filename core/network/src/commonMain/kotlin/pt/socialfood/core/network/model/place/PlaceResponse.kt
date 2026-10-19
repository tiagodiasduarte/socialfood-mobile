package pt.socialfood.core.network.model.place

import kotlinx.serialization.Serializable

@Serializable
data class PlaceResponse(val results: List<Place>) {
    @Serializable
    data class Place(val id: String, val name: String, val address: String)
}
