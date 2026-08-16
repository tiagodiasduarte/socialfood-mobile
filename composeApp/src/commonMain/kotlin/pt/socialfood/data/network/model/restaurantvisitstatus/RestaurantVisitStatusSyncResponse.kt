package pt.socialfood.data.network.model.restaurantvisitstatus

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantVisitStatusSyncResponse(
    val addedIds: List<String> = emptyList(),
    val removedIds: List<String> = emptyList(),
    val syncedAt: String,
)
