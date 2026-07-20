package pt.socialfood.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val description: String?,
    val cuisine: String = "Unknow",
    val city: String,
    val country: String,
    val countryCode: String,
    val postalCode: String?,
    val photoNames: List<String>,
    val address: String,
    val rating: Double,
    val userRatingCount: Int,
    val websiteUrl: String?,
    val phoneNumber: String,
    val regularOpeningHours: List<String>? = null,
)