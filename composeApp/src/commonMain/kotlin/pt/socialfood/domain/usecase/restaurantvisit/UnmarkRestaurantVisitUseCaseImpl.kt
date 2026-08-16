package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitsRepository

class UnmarkRestaurantVisitUseCaseImpl(private val repository: RestaurantVisitsRepository) :
    UnmarkRestaurantVisitUseCase {
    override suspend fun invoke(restaurantId: String, status: VisitStatus): Result<Unit> =
        repository.unmark(restaurantId, status)
}
