package pt.socialfood.data.network.model.favourite

import kotlinx.serialization.Serializable

@Serializable
data class FavouriteRestaurantSyncResponse(
    val addedRestaurantIds: List<String> = emptyList(),
    val removedRestaurantIds: List<String> = emptyList(),
    val nextCheckpoint: String,
)
