package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.usecase.restaurantvisit.GetRestaurantVisitsUseCase

class FakeGetRestaurantVisitsUseCase(
    private val result: (page: Int) -> Result<PagedRestaurantVisits> = {
        Result.Success(PagedRestaurantVisits(visits = emptyList(), page = it, total = 0, hasMore = false))
    },
) : GetRestaurantVisitsUseCase {
    var invokeCount: Int = 0
        private set
    var lastStatus: RestaurantVisitStatus? = null
        private set

    override suspend operator fun invoke(
        status: RestaurantVisitStatus,
        page: Int,
        limit: Int,
    ): Result<PagedRestaurantVisits> {
        invokeCount++
        lastStatus = status
        return result(page)
    }
}
