package pt.socialfood.core.data.mapper

import pt.socialfood.core.model.GuideSuggestions
import pt.socialfood.core.model.RestaurantSuggestions
import pt.socialfood.core.network.model.search.GuideSuggestionsResponse
import pt.socialfood.core.network.model.search.RestaurantSuggestionsResponse

fun RestaurantSuggestionsResponse.toRestaurantSuggestions(): RestaurantSuggestions = RestaurantSuggestions(
    restaurants = restaurants.map { it.toRestaurant() },
    generatedAt = generatedAt,
)

fun GuideSuggestionsResponse.toGuideSuggestions(): GuideSuggestions = GuideSuggestions(
    guides = guides.map { it.toGuide() },
    generatedAt = generatedAt,
)
