package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitsRepository

class SyncRestaurantVisitsUseCaseImpl(private val repository: RestaurantVisitsRepository) :
    SyncRestaurantVisitsUseCase {
    override suspend operator fun invoke(status: VisitStatus): Result<Unit> = repository.sync(status)
}
