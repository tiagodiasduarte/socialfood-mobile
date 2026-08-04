package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteRestaurant
import pt.socialfood.domain.model.Restaurant

fun randomFavouriteRestaurant(restaurant: Restaurant = randomRestaurant(), favouritedAt: Long = randomLong()) =
    FavouriteRestaurant(restaurant = restaurant, favouritedAt = favouritedAt)
