package pt.socialfood.data.network.model.wishlist

import kotlinx.serialization.Serializable

@Serializable
data class WishlistSyncResponse(
    val addedIds: List<String> = emptyList(),
    val removedIds: List<String> = emptyList(),
    val syncedAt: String,
)
