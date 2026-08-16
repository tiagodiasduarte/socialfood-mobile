package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus

interface RestaurantVisitsRepository {
    suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit>

    suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit>

    suspend fun getPaged(status: VisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisits>

    suspend fun sync(status: VisitStatus): Result<Unit>
}
