package pt.socialfood.data.network.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class CreateGuideRequest(
    val name: String,
    val description: String,
    val userId: String,
)
