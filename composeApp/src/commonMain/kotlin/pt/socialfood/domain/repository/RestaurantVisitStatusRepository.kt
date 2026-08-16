package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisitStatuses
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus

interface RestaurantVisitStatusRepository {
    suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit>

    suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit>

    suspend fun getPaged(status: VisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisitStatuses>

    suspend fun sync(status: VisitStatus): Result<Unit>
}
