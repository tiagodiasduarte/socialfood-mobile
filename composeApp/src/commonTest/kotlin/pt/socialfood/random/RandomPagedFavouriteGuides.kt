package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.PagedFavouriteGuides
import kotlin.random.Random

fun Random.nextPagedFavouriteGuides(
    favourites: List<FavouriteGuide> = nextList { nextFavouriteGuide() },
    page: Int = nextInt(1, 10),
    total: Int = favourites.size,
    hasMore: Boolean = nextBoolean(),
) = PagedFavouriteGuides(favourites = favourites, page = page, total = total, hasMore = hasMore)
