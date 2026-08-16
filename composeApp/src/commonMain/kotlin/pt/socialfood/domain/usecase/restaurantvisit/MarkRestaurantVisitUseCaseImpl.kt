package pt.socialfood.domain.usecase.restaurantvisit

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.RestaurantVisitsRepository

class MarkRestaurantVisitUseCaseImpl(private val repository: RestaurantVisitsRepository) :
    MarkRestaurantVisitUseCase {
    override suspend operator fun invoke(restaurant: Restaurant, status: VisitStatus): Result<Unit> =
        repository.mark(restaurant, status)
}
