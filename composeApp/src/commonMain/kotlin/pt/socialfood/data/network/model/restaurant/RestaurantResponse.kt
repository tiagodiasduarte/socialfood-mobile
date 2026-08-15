package pt.socialfood.data.network.model.restaurant

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantResponse(
    val id: String,
    val name: String,
    val description: String?,
    val photoNames: List<String>,
    val city: String,
    val country: String,
    val countryCode: String,
    val postalCode: String?,
    val phoneNumber: String,
    val address: String,
    val rating: Double,
    val userRatingCount: Int,
    val websiteUrl: String?,
    val location: Location,
    val regularOpeningHours: List<String>?,
) {
    @Serializable
    data class Location(val latitude: Double, val longitude: Double)
}
