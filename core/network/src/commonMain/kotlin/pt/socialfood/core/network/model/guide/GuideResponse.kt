package pt.socialfood.core.network.model.guide

import kotlinx.serialization.Serializable
import pt.socialfood.core.model.GuideVisibility
import pt.socialfood.core.network.model.author.AuthorResponse

@Serializable
data class GuideResponse(
    val id: String,
    val name: String,
    val description: String,
    val visibility: GuideVisibility,
    val author: AuthorResponse,
    val numberOfRestaurants: Int,
    val imageUrl: String? = null,
    val shareCode: String? = null,
)
