package pt.socialfood.data.network.model.restaurantvisit

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantVisitSyncResponse(
    val addedIds: List<String> = emptyList(),
    val removedIds: List<String> = emptyList(),
    val syncedAt: String,
)
