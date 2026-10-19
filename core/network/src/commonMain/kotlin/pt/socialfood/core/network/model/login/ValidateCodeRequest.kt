package pt.socialfood.core.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class ValidateCodeRequest(val email: String, val code: String)
