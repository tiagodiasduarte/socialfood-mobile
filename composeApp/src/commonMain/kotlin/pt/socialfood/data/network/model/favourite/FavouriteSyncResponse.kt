package pt.socialfood.data.network.model.favourite

import kotlinx.serialization.Serializable

@Serializable
data class FavouriteSyncResponse(
    val addedIds: List<String> = emptyList(),
    val removedIds: List<String> = emptyList(),
    val nextCheckpoint: String,
)
