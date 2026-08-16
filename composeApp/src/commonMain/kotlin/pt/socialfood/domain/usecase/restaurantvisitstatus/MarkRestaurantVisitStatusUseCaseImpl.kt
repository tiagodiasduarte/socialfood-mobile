package pt.socialfood.domain.usecase.restaurantvisitstatus

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

class MarkRestaurantVisitStatusUseCaseImpl(private val repository: RestaurantVisitStatusRepository) :
    MarkRestaurantVisitStatusUseCase {
    override suspend operator fun invoke(restaurant: Restaurant, status: VisitStatus): Result<Unit> =
        repository.mark(restaurant, status)
}
