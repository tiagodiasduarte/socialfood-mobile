package pt.socialfood.random

import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import kotlin.random.Random

fun Random.nextRestaurantVisitStatus(
    restaurant: Restaurant = nextRestaurant(),
    status: VisitStatus = nextEnum(),
    recordedAt: Long = nextLong(),
) = RestaurantVisitStatus(restaurant = restaurant, status = status, recordedAt = recordedAt)
