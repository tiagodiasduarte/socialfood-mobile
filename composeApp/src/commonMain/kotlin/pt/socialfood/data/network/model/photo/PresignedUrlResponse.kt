package pt.socialfood.data.network.model.photo

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponse(val uploadUrl: String, val key: String, val publicUrl: String)
