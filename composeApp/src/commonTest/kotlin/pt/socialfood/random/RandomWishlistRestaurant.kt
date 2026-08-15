package pt.socialfood.random

import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.WishlistRestaurant
import kotlin.random.Random

fun Random.nextWishlistRestaurant(restaurant: Restaurant = nextRestaurant(), wishlistedAt: Long = nextLong()) =
    WishlistRestaurant(restaurant = restaurant, wishlistedAt = wishlistedAt)
