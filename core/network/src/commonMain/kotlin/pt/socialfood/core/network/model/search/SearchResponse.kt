package pt.socialfood.core.network.model.search

import kotlinx.serialization.Serializable
import pt.socialfood.core.network.model.PagedResponse
import pt.socialfood.core.network.model.author.AuthorResponse
import pt.socialfood.core.network.model.guide.GuideResponse
import pt.socialfood.core.network.model.restaurant.RestaurantResponse

@Serializable
data class SearchResponse(
    val authors: PagedResponse<AuthorResponse>,
    val guides: PagedResponse<GuideResponse>,
    val restaurants: PagedResponse<RestaurantResponse>,
)
