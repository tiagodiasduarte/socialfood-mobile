package pt.socialfood.core.network.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPhotoRequest(val imageUrl: String)
