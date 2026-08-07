package pt.socialfood.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Authors : Route

    @Serializable
    data object Guides : Route

    @Serializable
    data object Home : Route

    @Serializable
    data class GuideDetail(val guideId: String) : Route

    @Serializable
    data class AuthorDetail(val authorId: String) : Route

    @Serializable
    data object CreateGuide : Route

    @Serializable
    data class EditGuide(val guideId: String, val initialTab: Int = 0) : Route

    @Serializable
    data class AddRestaurants(val guideId: String) : Route

    @Serializable
    data class RestaurantDetail(val restaurantId: String) : Route

    @Serializable
    data object EditProfile : Route

    @Serializable
    data object FavouriteGuides : Route

    @Serializable
    data object FavouriteRestaurants : Route
}
