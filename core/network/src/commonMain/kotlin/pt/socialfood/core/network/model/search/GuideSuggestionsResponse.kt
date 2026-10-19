package pt.socialfood.core.network.model.search

import kotlinx.serialization.Serializable
import pt.socialfood.core.network.model.guide.GuideResponse

@Serializable
data class GuideSuggestionsResponse(val guides: List<GuideResponse>, val generatedAt: String)
