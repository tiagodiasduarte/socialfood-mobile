package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetRestaurantVisitStatusListUseCase

class FakeGetRestaurantVisitStatusListUseCase(
    private val result: (status: VisitStatus) -> Flow<List<Restaurant>> = { flowOf(emptyList()) },
) : GetRestaurantVisitStatusListUseCase {
    var lastStatus: VisitStatus? = null
        private set

    override operator fun invoke(status: VisitStatus): Flow<List<Restaurant>> {
        lastStatus = status
        return result(status)
    }
}
