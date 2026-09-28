package pt.socialfood.presentation.guide.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface GuideRoute : NavKey {

    @Serializable
    data object Guides : GuideRoute

    @Serializable
    data class GuideDetail(val guideId: String) : GuideRoute

    @Serializable
    data class GuideMap(val guideId: String, val guideName: String, val restaurantsCount: Int) : GuideRoute

    @Serializable
    data object CreateGuide : GuideRoute

    @Serializable
    data class EditGuide(val guideId: String, val initialTab: Int = 0) : GuideRoute
}
