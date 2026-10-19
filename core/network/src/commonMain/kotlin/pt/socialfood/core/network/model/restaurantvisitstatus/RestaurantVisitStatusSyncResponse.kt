package pt.socialfood.core.network.model.restaurantvisitstatus

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantVisitStatusSyncResponse(
    val updated: List<RestaurantStatusEntry> = emptyList(),
    val removedIds: List<String> = emptyList(),
    val syncedAt: String,
) {

    @Serializable
    data class RestaurantStatusEntry(val id: String, val status: String)
}
