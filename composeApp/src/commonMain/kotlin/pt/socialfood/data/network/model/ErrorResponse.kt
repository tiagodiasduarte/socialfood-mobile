package pt.socialfood.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(val error: String, val message: String)
