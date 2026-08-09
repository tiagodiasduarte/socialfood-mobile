package pt.socialfood.data.network.model.search

import kotlinx.serialization.Serializable
import pt.socialfood.data.network.model.PagedResponse
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.data.network.model.guide.GuideResponse
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

@Serializable
data class SearchResponse(
    val authors: PagedResponse<AuthorResponse>,
    val guides: PagedResponse<GuideResponse>,
    val restaurants: PagedResponse<RestaurantResponse>,
)
