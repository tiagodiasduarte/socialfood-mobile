package pt.socialfood.data.network.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val username: String?,
    val imageUrl: String? = null,
    val firstName: String?,
    val lastName: String?,
    val bio: String?,
    val phoneNumber: String?,
    val country: String?,
    val city: String?,
    val address: String?,
    val role: String = "",
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
    val isVerified: Boolean = true,
)