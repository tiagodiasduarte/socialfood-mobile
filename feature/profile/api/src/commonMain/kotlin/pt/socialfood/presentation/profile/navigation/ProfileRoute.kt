package pt.socialfood.presentation.profile.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface ProfileRoute : NavKey {

    @Serializable
    data object EditProfile : ProfileRoute
}
