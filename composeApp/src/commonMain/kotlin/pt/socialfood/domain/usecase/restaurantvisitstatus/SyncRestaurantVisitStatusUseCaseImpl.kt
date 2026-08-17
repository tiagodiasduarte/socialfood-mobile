package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class SyncRestaurantVisitStatusUseCaseImpl(private val repository: RestaurantVisitStatusRepository) :
    SyncRestaurantVisitStatusUseCase {
    override suspend operator fun invoke(status: VisitStatus): Result<Unit> = repository.sync(status)
}
