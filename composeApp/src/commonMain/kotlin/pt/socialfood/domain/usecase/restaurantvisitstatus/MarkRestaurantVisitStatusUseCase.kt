package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus

interface MarkRestaurantVisitStatusUseCase {
    suspend operator fun invoke(restaurant: Restaurant, status: VisitStatus): Result<Unit>
}
