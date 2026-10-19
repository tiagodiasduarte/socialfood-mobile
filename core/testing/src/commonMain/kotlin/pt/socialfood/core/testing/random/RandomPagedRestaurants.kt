package pt.socialfood.core.testing.random

import pt.socialfood.core.model.PagedRestaurants
import pt.socialfood.core.model.Restaurant
import kotlin.random.Random

fun Random.nextPagedRestaurants(
    restaurants: List<Restaurant> = nextList { nextRestaurant() },
    page: Int = nextInt(1, 10),
    total: Int = restaurants.size,
    hasMore: Boolean = nextBoolean(),
) = PagedRestaurants(restaurants = restaurants, page = page, total = total, hasMore = hasMore)
