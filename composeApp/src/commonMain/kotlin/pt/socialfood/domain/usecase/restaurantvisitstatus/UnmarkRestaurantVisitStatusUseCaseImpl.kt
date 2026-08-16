package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class UnmarkRestaurantVisitStatusUseCaseImpl(private val repository: RestaurantVisitStatusRepository) :
    UnmarkRestaurantVisitStatusUseCase {
    override suspend fun invoke(restaurantId: String, status: VisitStatus): Result<Unit> =
        repository.unmark(restaurantId, status)
}
