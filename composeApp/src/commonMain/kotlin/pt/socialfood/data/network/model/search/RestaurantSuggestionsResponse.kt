package pt.socialfood.data.network.model.search

import kotlinx.serialization.Serializable
import pt.socialfood.data.network.model.restaurant.RestaurantResponse

@Serializable
data class RestaurantSuggestionsResponse(val restaurants: List<RestaurantResponse>, val generatedAt: String)
