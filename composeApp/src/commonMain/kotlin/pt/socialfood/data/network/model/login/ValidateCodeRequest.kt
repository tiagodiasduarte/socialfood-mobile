package pt.socialfood.data.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class ValidateCodeRequest(val email: String, val code: String)
