package pt.socialfood.data.network.model.photo

import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlRequest(val fileName: String, val mimeType: String, val context: String)
