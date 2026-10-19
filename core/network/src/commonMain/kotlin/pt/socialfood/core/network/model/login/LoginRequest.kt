package pt.socialfood.core.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val email: String, val password: String)
