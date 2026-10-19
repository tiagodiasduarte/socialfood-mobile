package pt.socialfood.core.testing.random

import pt.socialfood.core.model.Restaurant
import pt.socialfood.core.model.RestaurantVisitStatus
import pt.socialfood.core.model.VisitStatus
import kotlin.random.Random

fun Random.nextRestaurantVisitStatus(
    restaurant: Restaurant = nextRestaurant(),
    status: VisitStatus = nextEnum(),
    recordedAt: Long = nextLong(),
) = RestaurantVisitStatus(restaurant = restaurant, status = status, recordedAt = recordedAt)
