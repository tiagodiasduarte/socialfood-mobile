package pt.socialfood.data.network.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGuidePhotoRequest(val imageUrl: String)