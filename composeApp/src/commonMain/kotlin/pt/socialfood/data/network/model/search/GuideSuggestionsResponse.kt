package pt.socialfood.data.network.model.search

import kotlinx.serialization.Serializable
import pt.socialfood.data.network.model.guide.GuideResponse

@Serializable
data class GuideSuggestionsResponse(val guides: List<GuideResponse>, val generatedAt: String)
