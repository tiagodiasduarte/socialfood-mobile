package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class SyncRestaurantVisitStatusUseCaseImpl(private val repository: RestaurantVisitStatusRepository) :
    SyncRestaurantVisitStatusUseCase {
    override suspend operator fun invoke(): Result<Unit> = repository.sync()
}
