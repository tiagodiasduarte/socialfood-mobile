package pt.socialfood.presentation.favourite.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface FavouriteRoute : NavKey {

    @Serializable
    data object FavouriteGuides : FavouriteRoute

    @Serializable
    data object FavouriteRestaurants : FavouriteRoute
}
