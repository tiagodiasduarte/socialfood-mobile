package pt.socialfood.data.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class RegisterResponse(val userId: String, val token: String)
