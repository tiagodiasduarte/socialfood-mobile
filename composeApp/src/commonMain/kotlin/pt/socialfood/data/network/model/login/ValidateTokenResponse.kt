package pt.socialfood.data.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class ValidateTokenResponse(
    val name: String,
    val email: String,
    val token: String,
)
