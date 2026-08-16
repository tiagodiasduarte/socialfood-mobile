package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus

interface SyncRestaurantVisitsUseCase {
    suspend operator fun invoke(status: VisitStatus): Result<Unit>
}
