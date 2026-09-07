package pt.socialfood.data.network.model.favourite

import kotlinx.serialization.Serializable

@Serializable
data class FavouriteSyncResponse<T>(
    val added: List<T> = emptyList(),
    val removedIds: List<String> = emptyList(),
    val syncedAt: String,
)
