package pt.socialfood.random

import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.PagedFavouriteGuides

fun randomPagedFavouriteGuides(
    favourites: List<FavouriteGuide> = randomList { randomFavouriteGuide() },
    page: Int = randomInt(1, 10),
    total: Int = favourites.size,
    hasMore: Boolean = randomBoolean(),
) = PagedFavouriteGuides(favourites = favourites, page = page, total = total, hasMore = hasMore)
