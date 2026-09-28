package pt.socialfood.presentation.home.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface HomeRoute : NavKey {

    @Serializable
    data object Home : HomeRoute
}
