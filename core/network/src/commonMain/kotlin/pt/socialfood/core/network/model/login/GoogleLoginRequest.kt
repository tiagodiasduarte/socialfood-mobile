package pt.socialfood.core.network.model.login

import kotlinx.serialization.Serializable

@Serializable
data class GoogleLoginRequest(val idToken: String)
