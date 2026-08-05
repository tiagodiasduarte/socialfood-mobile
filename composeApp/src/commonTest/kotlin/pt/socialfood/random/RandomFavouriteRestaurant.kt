package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteRestaurant
import pt.socialfood.domain.model.Restaurant
import kotlin.random.Random

fun Random.nextFavouriteRestaurant(restaurant: Restaurant = nextRestaurant(), favouritedAt: Long = nextLong()) =
    FavouriteRestaurant(restaurant = restaurant, favouritedAt = favouritedAt)
