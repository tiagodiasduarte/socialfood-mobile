package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisitStatuses
import pt.socialfood.domain.model.VisitStatus

interface GetRestaurantVisitStatusUseCase {
    suspend operator fun invoke(status: VisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisitStatuses>
}
