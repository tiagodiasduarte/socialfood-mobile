package pt.socialfood.data.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class ResendVerificationCodeRequest(val email: String)
