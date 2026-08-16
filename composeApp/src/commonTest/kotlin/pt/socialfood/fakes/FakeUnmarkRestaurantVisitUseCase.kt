package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.usecase.restaurantvisit.UnmarkRestaurantVisitUseCase

class FakeUnmarkRestaurantVisitUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    UnmarkRestaurantVisitUseCase {
    var lastUnmarkedRestaurantId: String? = null
        private set
    var lastStatus: RestaurantVisitStatus? = null
        private set

    override suspend operator fun invoke(restaurantId: String, status: RestaurantVisitStatus): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        lastStatus = status
        return result
    }
}
