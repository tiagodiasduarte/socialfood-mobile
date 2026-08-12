package pt.socialfood.mapper

import pt.socialfood.data.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.domain.model.RestaurantSuggestions

fun RestaurantSuggestionsResponse.toRestaurantSuggestions(): RestaurantSuggestions = RestaurantSuggestions(
    restaurants = restaurants.map { it.toRestaurant() },
    generatedAt = generatedAt,
)
