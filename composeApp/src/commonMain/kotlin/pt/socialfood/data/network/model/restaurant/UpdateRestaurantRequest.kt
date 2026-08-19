package pt.socialfood.data.network.model.restaurant

import kotlinx.serialization.Serializable

@Serializable
data class UpdateRestaurantRequest(
    val name: String,
    val description: String?,
    val country: String,
    val city: String,
    val address: String,
    val phoneNumber: String,
    val websiteUrl: String,
)
