package pt.socialfood.core.testing.random

import pt.socialfood.core.model.Restaurant
import pt.socialfood.core.model.RestaurantSuggestions
import kotlin.random.Random

fun Random.nextRestaurantSuggestions(
    restaurants: List<Restaurant> = nextList { nextRestaurant() },
    generatedAt: String = nextString(),
) = RestaurantSuggestions(restaurants = restaurants, generatedAt = generatedAt)
