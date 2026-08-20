package pt.socialfood.data.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(val token: String, val refreshToken: String)
