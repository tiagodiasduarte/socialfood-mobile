package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedRestaurantVisits
import pt.socialfood.domain.model.RestaurantVisitStatus

interface GetRestaurantVisitsUseCase {
    suspend operator fun invoke(status: RestaurantVisitStatus, page: Int, limit: Int): Result<PagedRestaurantVisits>
}
