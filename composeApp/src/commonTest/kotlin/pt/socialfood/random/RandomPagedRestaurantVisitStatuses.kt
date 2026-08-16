package pt.socialfood.random

import pt.socialfood.domain.model.PagedRestaurantVisitStatuses
import pt.socialfood.domain.model.RestaurantVisitStatus
import kotlin.random.Random

fun Random.nextPagedRestaurantVisitStatuses(
    visits: List<RestaurantVisitStatus> = nextList { nextRestaurantVisitStatus() },
    page: Int = nextInt(1, 10),
    total: Int = visits.size,
    hasMore: Boolean = nextBoolean(),
) = PagedRestaurantVisitStatuses(visits = visits, page = page, total = total, hasMore = hasMore)
