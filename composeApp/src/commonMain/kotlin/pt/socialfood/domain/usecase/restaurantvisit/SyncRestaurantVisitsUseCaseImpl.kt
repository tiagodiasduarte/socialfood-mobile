package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.repository.RestaurantVisitsRepository

class SyncRestaurantVisitsUseCaseImpl(private val repository: RestaurantVisitsRepository) :
    SyncRestaurantVisitsUseCase {
    override suspend operator fun invoke(status: RestaurantVisitStatus): Result<Unit> = repository.sync(status)
}
