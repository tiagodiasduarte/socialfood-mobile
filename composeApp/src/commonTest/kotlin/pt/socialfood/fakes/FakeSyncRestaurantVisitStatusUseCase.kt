package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.restaurantvisitstatus.SyncRestaurantVisitStatusUseCase

class FakeSyncRestaurantVisitStatusUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    SyncRestaurantVisitStatusUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<Unit> {
        invokeCount++
        return result
    }
}
