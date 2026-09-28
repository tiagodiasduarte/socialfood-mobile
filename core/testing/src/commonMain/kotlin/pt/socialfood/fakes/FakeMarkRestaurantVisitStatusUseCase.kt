package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.MarkRestaurantVisitStatusUseCase

class FakeMarkRestaurantVisitStatusUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    MarkRestaurantVisitStatusUseCase {
    var lastMarkedRestaurant: Restaurant? = null
        private set
    var lastStatus: VisitStatus? = null
        private set

    override suspend operator fun invoke(restaurant: Restaurant, status: VisitStatus): Result<Unit> {
        lastMarkedRestaurant = restaurant
        lastStatus = status
        return result
    }
}
