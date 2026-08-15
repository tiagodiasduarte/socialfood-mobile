package pt.socialfood.data.network.model.author

import kotlinx.serialization.Serializable
import pt.socialfood.data.network.model.guide.GuideResponse

@Serializable
data class AuthorDetailResponse(
    val id: String,
    val name: String,
    val username: String,
    val imageUrl: String? = null,
    val guidesCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val isFollowing: Boolean = false,
    val facebookUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
    val guides: List<GuideResponse> = emptyList(),
) {
    @Serializable
    data class GuideResponse(
        val id: String,
        val name: String,
        val description: String,
        val numberOfRestaurants: Int,
        val imageUrl: String? = null,
    )
}
