package pt.socialfood.data.network.model.favourite

import kotlinx.serialization.Serializable
import pt.socialfood.data.network.model.guide.GuideResponse

@Serializable
data class FavouriteChangesResponse(
    val added: List<GuideResponse> = emptyList(),
    val removedGuideIds: List<String> = emptyList(),
    val nextCheckpoint: String,
    val hasMore: Boolean = false,
)
