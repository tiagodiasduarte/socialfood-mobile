package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class FakeRestaurantVisitStatusRepository(
    private val markResult: Result<Unit> = Result.Success(Unit),
    private val unmarkResult: Result<Unit> = Result.Success(Unit),
    private val statusResult: Result<VisitStatus?> = Result.Success(null),
    private val syncResult: Result<Unit> = Result.Success(Unit),
    private val pagingFlow: (status: VisitStatus) -> Flow<PagingData<RestaurantVisitStatus>> =
        { flowOf(PagingData.empty()) },
    private val allFlow: (status: VisitStatus) -> Flow<List<Restaurant>> = { flowOf(emptyList()) },
) : RestaurantVisitStatusRepository {

    var lastMarkedRestaurant: Restaurant? = null
        private set
    var lastMarkedStatus: VisitStatus? = null
        private set

    var lastUnmarkedRestaurantId: String? = null
        private set
    var lastUnmarkedStatus: VisitStatus? = null
        private set

    var pagingInvokeCount: Int = 0
        private set
    var lastPagingStatus: VisitStatus? = null
        private set

    var lastAllFlowStatus: VisitStatus? = null
        private set

    var syncInvokeCount: Int = 0
        private set

    override suspend fun mark(restaurant: Restaurant, status: VisitStatus): Result<Unit> {
        lastMarkedRestaurant = restaurant
        lastMarkedStatus = status
        return markResult
    }

    override suspend fun unmark(restaurantId: String, status: VisitStatus): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        lastUnmarkedStatus = status
        return unmarkResult
    }

    override suspend fun getStatus(restaurantId: String): Result<VisitStatus?> = statusResult

    override fun getPagingFlow(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>> {
        pagingInvokeCount++
        lastPagingStatus = status
        return pagingFlow(status)
    }

    override fun getAllFlow(status: VisitStatus): Flow<List<Restaurant>> {
        lastAllFlowStatus = status
        return allFlow(status)
    }

    override suspend fun sync(): Result<Unit> {
        syncInvokeCount++
        return syncResult
    }
}
