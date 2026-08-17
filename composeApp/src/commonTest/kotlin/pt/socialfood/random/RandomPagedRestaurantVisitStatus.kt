package pt.socialfood.random

import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.RestaurantVisitStatus
import kotlin.random.Random

fun Random.nextPagedRestaurantVisitStatus(
    visits: List<RestaurantVisitStatus> = nextList { nextRestaurantVisitStatus() },
    page: Int = nextInt(1, 10),
    total: Int = visits.size,
    hasMore: Boolean = nextBoolean(),
) = PagedRestaurantVisitStatus(visits = visits, page = page, total = total, hasMore = hasMore)
