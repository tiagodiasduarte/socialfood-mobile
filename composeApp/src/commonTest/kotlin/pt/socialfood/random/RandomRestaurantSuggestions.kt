package pt.socialfood.random

import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantSuggestions
import kotlin.random.Random

fun Random.nextRestaurantSuggestions(
    restaurants: List<Restaurant> = nextList { nextRestaurant() },
    generatedAt: String = nextString(),
) = RestaurantSuggestions(restaurants = restaurants, generatedAt = generatedAt)
