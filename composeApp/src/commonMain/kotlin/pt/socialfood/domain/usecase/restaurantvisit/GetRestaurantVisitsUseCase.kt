package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.VisitStatus

interface GetRestaurantVisitsUseCase {
    suspend operator fun invoke(status: VisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisits>
}
