package pt.socialfood.data.network.model.restaurantvisitstatus

import kotlinx.serialization.Serializable
import pt.socialfood.domain.model.VisitStatus

@Serializable
data class RestaurantVisitStatusSyncResponse(
    val updated: List<RestaurantStatusEntry> = emptyList(),
    val removedIds: List<String> = emptyList(),
    val syncedAt: String,
) {

    @Serializable
    data class RestaurantStatusEntry(val restaurantId: String, val status: VisitStatus)
}
