package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.UnmarkRestaurantVisitStatusUseCase

class FakeUnmarkRestaurantVisitStatusUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    UnmarkRestaurantVisitStatusUseCase {
    var lastUnmarkedRestaurantId: String? = null
        private set
    var lastStatus: VisitStatus? = null
        private set

    override suspend operator fun invoke(restaurantId: String, status: VisitStatus): Result<Unit> {
        lastUnmarkedRestaurantId = restaurantId
        lastStatus = status
        return result
    }
}
