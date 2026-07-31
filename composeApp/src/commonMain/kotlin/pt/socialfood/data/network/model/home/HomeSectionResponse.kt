package pt.socialfood.data.network.model.home

import kotlinx.serialization.Serializable
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

@Serializable
data class HomeSectionResponse(
    val id: String,
    val title: String,
    val type: String,
    val position: Int,
    val isActive: Boolean,
    val items: List<HomeSectionItemResponse> = emptyList(),
)

@Serializable
data class HomeSectionItemResponse(
    val id: String,
    val sectionId: String = "",
    val itemId: String = "",
    val itemType: String,
    val position: Int,
    val restaurant: RestaurantResponse? = null,
    val guide: GuideResponse? = null,
)
