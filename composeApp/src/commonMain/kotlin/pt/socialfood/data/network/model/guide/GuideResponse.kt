package pt.socialfood.data.network.model.guide

import kotlinx.serialization.Serializable
import pt.socialfood.data.network.model.author.AuthorResponse
import pt.socialfood.domain.model.GuideVisibility

@Serializable
data class GuideResponse(
    val id: String,
    val name: String,
    val description: String,
    val visibility: GuideVisibility,
    val author: AuthorResponse,
    val numberOfRestaurants: Int,
    val imageUrl: String? = null,
)
