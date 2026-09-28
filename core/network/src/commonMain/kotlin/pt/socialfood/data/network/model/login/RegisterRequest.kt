package pt.socialfood.data.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val name: String, val email: String, val password: String)
