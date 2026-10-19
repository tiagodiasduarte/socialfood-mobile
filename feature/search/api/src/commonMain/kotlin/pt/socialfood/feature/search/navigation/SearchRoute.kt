package pt.socialfood.feature.search.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface SearchRoute : NavKey {

    @Serializable
    data object Search : SearchRoute
}
