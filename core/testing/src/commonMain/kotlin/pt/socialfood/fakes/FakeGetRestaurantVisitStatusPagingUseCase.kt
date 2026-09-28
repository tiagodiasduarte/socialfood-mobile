package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetRestaurantVisitStatusPagingUseCase

class FakeGetRestaurantVisitStatusPagingUseCase(
    private val result: (status: VisitStatus) -> Flow<PagingData<RestaurantVisitStatus>> =
        { flowOf(PagingData.empty()) },
) : GetRestaurantVisitStatusPagingUseCase {
    var invokeCount: Int = 0
        private set
    var lastStatus: VisitStatus? = null
        private set

    override operator fun invoke(status: VisitStatus): Flow<PagingData<RestaurantVisitStatus>> {
        invokeCount++
        lastStatus = status
        return result(status)
    }
}
