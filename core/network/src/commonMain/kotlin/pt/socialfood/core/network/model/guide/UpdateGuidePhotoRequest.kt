package pt.socialfood.core.network.model.guide

import kotlinx.serialization.Serializable

@Serializable
data class UpdateGuidePhotoRequest(val imageUrl: String)
