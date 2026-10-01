package pt.socialfood.presentation.restaurant.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface RestaurantRoute : NavKey {

    @Serializable
    data class RestaurantDetail(val restaurantId: String) : RestaurantRoute

    /** Searches for a restaurant and publishes it to [RestaurantPickerResults] under [requestKey]. */
    @Serializable
    data class PickRestaurant(val requestKey: String) : RestaurantRoute

    @Serializable
    data object WishRestaurants : RestaurantRoute

    @Serializable
    data object VisitedRestaurants : RestaurantRoute
}
