package pt.socialfood.data.network.model.author

import kotlinx.serialization.Serializable

@Serializable
data class AuthorResponse(
    val id: String,
    val name: String,
    val username: String,
    val imageUrl: String? = null,
)
