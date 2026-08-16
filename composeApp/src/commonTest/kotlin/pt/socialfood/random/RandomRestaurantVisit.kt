package pt.socialfood.random

import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisit
import pt.socialfood.domain.model.RestaurantVisitStatus
import kotlin.random.Random

fun Random.nextRestaurantVisit(
    restaurant: Restaurant = nextRestaurant(),
    status: RestaurantVisitStatus = nextEnum(),
    recordedAt: Long = nextLong(),
) = RestaurantVisit(restaurant = restaurant, status = status, recordedAt = recordedAt)
