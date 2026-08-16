package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantVisitStatus

interface UnmarkRestaurantVisitUseCase {
    suspend operator fun invoke(restaurantId: String, status: RestaurantVisitStatus): Result<Unit>
}
