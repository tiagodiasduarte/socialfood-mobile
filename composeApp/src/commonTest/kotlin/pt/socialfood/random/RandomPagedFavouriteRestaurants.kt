package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteRestaurant
import pt.socialfood.domain.model.PagedFavouriteRestaurants

fun randomPagedFavouriteRestaurants(
    favourites: List<FavouriteRestaurant> = randomList { randomFavouriteRestaurant() },
    page: Int = randomInt(1, 10),
    total: Int = favourites.size,
    hasMore: Boolean = randomBoolean(),
) = PagedFavouriteRestaurants(favourites = favourites, page = page, total = total, hasMore = hasMore)
