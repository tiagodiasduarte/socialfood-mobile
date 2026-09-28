package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class GetVisitStatusUseCaseImpl(private val repository: RestaurantVisitStatusRepository) : GetVisitStatusUseCase {
    override suspend fun invoke(restaurantId: String): Result<VisitStatus?> = repository.getStatus(restaurantId)
}
