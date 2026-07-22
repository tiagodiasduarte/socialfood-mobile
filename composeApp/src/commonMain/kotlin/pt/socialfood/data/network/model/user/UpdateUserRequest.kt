package pt.socialfood.data.network.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val role: String? = null,
    val imageUrl: String? = null,
    val name: String? = null,
    val city: String? = null,
    val country: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
)
