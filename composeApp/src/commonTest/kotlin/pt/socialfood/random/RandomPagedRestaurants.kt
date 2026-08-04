package pt.socialfood.random

import pt.socialfood.domain.model.PagedRestaurants
import pt.socialfood.domain.model.Restaurant

fun randomPagedRestaurants(
    restaurants: List<Restaurant> = randomList { randomRestaurant() },
    page: Int = randomInt(1, 10),
    total: Int = restaurants.size,
    hasMore: Boolean = randomBoolean(),
) = PagedRestaurants(restaurants = restaurants, page = page, total = total, hasMore = hasMore)
