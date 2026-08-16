package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.repository.RestaurantVisitsRepository

class FakeRestaurantVisitsRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val pagedResult: Result<PagedRestaurantVisits> = Result.Success(
        PagedRestaurantVisits(visits = emptyList(), page = 1, total = 0, hasMore = false),
    ),
    private val syncResult: Result<Unit> = Result.Success(Unit),
) : RestaurantVisitsRepository {

    var lastMarkedRestaurant: Restaurant? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set

    var lastStatus: RestaurantVisitStatus? = null
        private set

    override suspend fun mark(restaurant: Restaurant, status: RestaurantVisitStatus): Result<Unit> {
        lastMarkedRestaurant = restaurant
        lastStatus = status
        return markResult
    }

    override suspend fun unmark(restaurantId: String, status: RestaurantVisitStatus): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        lastStatus = status
        return unmarkResult
    }

    override suspend fun getPaged(status: RestaurantVisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisits> {
        lastStatus = status
        return pagedResult
    }

    override suspend fun sync(status: RestaurantVisitStatus): Result<Unit> {
        lastStatus = status
        return syncResult
    }
}
