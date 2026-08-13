package pt.socialfood.mapper

import pt.socialfood.data.network.model.search.GuideSuggestionsResponse
import pt.socialfood.data.network.model.search.RestaurantSuggestionsResponse
import pt.socialfood.domain.model.GuideSuggestions
import pt.socialfood.domain.model.RestaurantSuggestions

fun RestaurantSuggestionsResponse.toRestaurantSuggestions(): RestaurantSuggestions = RestaurantSuggestions(
    restaurants = restaurants.map { it.toRestaurant() },
    generatedAt = generatedAt,
)

fun GuideSuggestionsResponse.toGuideSuggestions(): GuideSuggestions = GuideSuggestions(
    guides = guides.map { it.toGuide() },
    generatedAt = generatedAt,
)
