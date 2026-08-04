package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteRestaurant
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import kotlin.random.Random

fun Random.nextPagedFavouriteRestaurants(
    favourites: List<FavouriteRestaurant> = nextList { nextFavouriteRestaurant() },
    page: Int = nextInt(1, 10),
    total: Int = favourites.size,
    hasMore: Boolean = nextBoolean(),
) = PagedFavouriteRestaurants(favourites = favourites, page = page, total = total, hasMore = hasMore)
