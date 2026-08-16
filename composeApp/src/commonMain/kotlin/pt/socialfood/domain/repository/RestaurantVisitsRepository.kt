package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus

interface RestaurantVisitsRepository {
    suspend fun mark(restaurant: Restaurant, status: RestaurantVisitStatus): Result<Unit>

    suspend fun unmark(restaurantId: String, status: RestaurantVisitStatus): Result<Unit>

    suspend fun getPaged(status: RestaurantVisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisits>

    suspend fun sync(status: RestaurantVisitStatus): Result<Unit>
}
