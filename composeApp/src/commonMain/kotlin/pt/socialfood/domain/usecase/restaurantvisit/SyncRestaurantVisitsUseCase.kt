package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantVisitStatus

interface SyncRestaurantVisitsUseCase {
    suspend operator fun invoke(status: RestaurantVisitStatus): Result<Unit>
}
