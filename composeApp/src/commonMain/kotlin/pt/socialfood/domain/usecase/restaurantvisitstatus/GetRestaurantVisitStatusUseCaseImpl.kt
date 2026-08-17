package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class GetRestaurantVisitStatusUseCaseImpl(private val repository: RestaurantVisitStatusRepository) :
    GetRestaurantVisitStatusUseCase {
    override suspend operator fun invoke(
        status: VisitStatus,
        page: Int,
        limit: Int,
    ): Result<PagedRestaurantVisitStatus> = repository.getPaged(status, page, limit)
}
