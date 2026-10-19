package pt.socialfood.core.network.model.search

import kotlinx.serialization.Serializable
import pt.socialfood.core.network.model.restaurant.RestaurantResponse

@Serializable
data class RestaurantSuggestionsResponse(val restaurants: List<RestaurantResponse>, val generatedAt: String)
