package pt.socialfood.presentation.restaurant.navigation

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Restaurant

/**
 * Hands the restaurant picked on [RestaurantRoute.PickRestaurant] back to whoever opened it. The
 * caller collects [results] with the request key it put in the route. A result published while
 * nobody is collecting is kept until the caller collects, e.g. after its ViewModel is recreated.
 */
interface RestaurantPickerResults {
    fun results(requestKey: String): Flow<Restaurant>

    fun publish(requestKey: String, restaurant: Restaurant)
}
