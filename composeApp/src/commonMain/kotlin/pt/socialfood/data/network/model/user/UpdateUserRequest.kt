package pt.socialfood.data.network.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val imageUrl: String? = null,
    val name: String? = null,
    val username: String? = null,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
    val isAuthor: Boolean? = null,
)
