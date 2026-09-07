package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class FakeRestaurantVisitStatusRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val pagingFlow: Flow<PagingData<RestaurantVisitStatus>> = emptyFlow(),
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

    override fun getPagingFlow(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>> {
        lastStatus = status
        return pagingFlow
    }

    override suspend fun sync(): Result<Unit> = syncResult
}
