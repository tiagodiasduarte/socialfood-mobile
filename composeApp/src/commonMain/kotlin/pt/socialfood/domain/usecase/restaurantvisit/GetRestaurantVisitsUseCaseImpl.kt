package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.repository.RestaurantVisitsRepository

class GetRestaurantVisitsUseCaseImpl(private val repository: RestaurantVisitsRepository) :
    GetRestaurantVisitsUseCase {
    override suspend operator fun invoke(
        status: RestaurantVisitStatus,
        page: Int,
        limit: Int,
    ): Result<PagedRestaurantVisits> = repository.getPaged(status, page, limit)
}
