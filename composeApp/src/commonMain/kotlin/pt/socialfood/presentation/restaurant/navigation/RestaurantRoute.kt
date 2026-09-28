package pt.socialfood.presentation.restaurant.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RestaurantRoute : NavKey {

    @Serializable
    data class RestaurantDetail(val restaurantId: String) : RestaurantRoute

    @Serializable
    data class AddRestaurants(val guideId: String) : RestaurantRoute

    @Serializable
    data object WishRestaurants : RestaurantRoute

    @Serializable
    data object AddWishRestaurant : RestaurantRoute

    @Serializable
    data object VisitedRestaurants : RestaurantRoute

    @Serializable
    data object AddVisitedRestaurant : RestaurantRoute
}
