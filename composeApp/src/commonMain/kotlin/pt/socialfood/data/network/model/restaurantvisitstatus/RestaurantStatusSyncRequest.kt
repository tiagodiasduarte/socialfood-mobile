package pt.socialfood.data.network.model.restaurantvisitstatus

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantStatusSyncRequest(
    val updated: List<RestaurantVisitStatusSyncResponse.RestaurantStatusEntry>,
    val removedIds: List<String>,
    val since: String,
)
