package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetRestaurantVisitStatusUseCase

class FakeGetRestaurantVisitStatusUseCase(
    private val result: (page: Int) -> Result<PagedRestaurantVisitStatus> = {
        Result.Success(PagedRestaurantVisitStatus(visits = emptyList(), page = it, total = 0, hasMore = false))
    },
) : GetRestaurantVisitStatusUseCase {
    var invokeCount: Int = 0
        private set
    var lastStatus: VisitStatus? = null
        private set

    override suspend operator fun invoke(
        status: VisitStatus,
        page: Int,
        limit: Int,
    ): Result<PagedRestaurantVisitStatus> {
        invokeCount++
        lastStatus = status
        return result(page)
    }
}
