package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus

interface UnmarkRestaurantVisitStatusUseCase {
    suspend operator fun invoke(restaurantId: String, status: VisitStatus): Result<Unit>
}
