package pt.socialfood.data.network.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val username: String,
    val imageUrl: String? = null,
    val role: String = "",
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
    val isVerified: Boolean = true,
    val isAuthor: Boolean = false,
)
