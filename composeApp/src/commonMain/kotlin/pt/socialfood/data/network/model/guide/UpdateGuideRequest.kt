package pt.socialfood.data.network.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGuideRequest(
    val name: String,
    val userId: String,
    val description: String,
    val restaurantIds: List<String>,
    val visibility: String,
)
