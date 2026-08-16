package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus

interface UnmarkRestaurantVisitUseCase {
    suspend operator fun invoke(restaurantId: String, status: VisitStatus): Result<Unit>
}
