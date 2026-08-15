package pt.socialfood.data.network.model.home

import kotlinx.serialization.Serializable

@Serializable
data class CreateHomeSectionRequest(val title: String, val type: String, val position: Int)

@Serializable
data class UpdateHomeSectionRequest(
    val title: String,
    val position: Int,
    val isActive: Boolean,
    val restaurantIds: List<String> = emptyList(),
    val guideIds: List<String> = emptyList(),
)

@Serializable
data class AddHomeSectionItemRequest(val itemId: String, val itemType: String, val position: Int)
