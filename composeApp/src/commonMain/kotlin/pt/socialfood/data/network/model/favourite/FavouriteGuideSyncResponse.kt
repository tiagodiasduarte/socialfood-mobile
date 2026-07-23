package pt.socialfood.data.network.model.favourite

import kotlinx.serialization.Serializable

@Serializable
data class FavouriteGuideSyncResponse(
    val addedGuideIds: List<String> = emptyList(),
    val removedGuideIds: List<String> = emptyList(),
    val nextCheckpoint: String,
)
