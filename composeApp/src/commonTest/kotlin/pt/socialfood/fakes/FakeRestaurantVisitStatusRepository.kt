package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class FakeRestaurantVisitStatusRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val pagedResult: Result<PagedRestaurantVisitStatus> = Result.Success(
        PagedRestaurantVisitStatus(visits = emptyList(), page = 1, total = 0, hasMore = false),
    ),
    private val syncResult: Result<Unit> = Result.Success(Unit),
    private val statusResult: Result<VisitStatus?> = Result.Success(null),
) : RestaurantVisitStatusRepository {

    var lastMarkedRestaurant: Restaurant? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set

    var lastStatus: VisitStatus? = null
        private set

    override suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit> {
        lastMarkedRestaurant = restaurant
        lastStatus = status
        return markResult
    }

    override suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        lastStatus = status
        return unmarkResult
    }

    override suspend fun getStatus(restaurantId: String): Result<VisitStatus?> = statusResult

    override suspend fun getPaged(status: VisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisitStatus> {
        lastStatus = status
        return pagedResult
    }

    override suspend fun sync(status: VisitStatus): Result<Unit> {
        lastStatus = status
        return syncResult
    }
}
