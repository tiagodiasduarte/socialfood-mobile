package pt.socialfood.core.network.model.guide

import kotlinx.serialization.Serializable
import pt.socialfood.core.model.GuideVisibility
import pt.socialfood.core.network.model.author.AuthorResponse
import pt.socialfood.core.network.model.restaurant.RestaurantResponse

@Serializable
data class GuideDetailResponse(
    val id: String,
    val name: String,
    val description: String,
    val visibility: GuideVisibility,
    val author: AuthorResponse,
    val restaurants: List<RestaurantResponse> = emptyList(),
    val imageUrl: String? = null,
    val shareCode: String? = null,
)
