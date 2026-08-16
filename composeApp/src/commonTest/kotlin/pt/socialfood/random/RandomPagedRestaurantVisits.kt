package pt.socialfood.random

import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.RestaurantVisitStatus
import kotlin.random.Random

fun Random.nextPagedRestaurantVisits(
    visits: List<RestaurantVisitStatus> = nextList { nextRestaurantVisitStatus() },
    page: Int = nextInt(1, 10),
    total: Int = visits.size,
    hasMore: Boolean = nextBoolean(),
) = PagedRestaurantVisits(visits = visits, page = page, total = total, hasMore = hasMore)
