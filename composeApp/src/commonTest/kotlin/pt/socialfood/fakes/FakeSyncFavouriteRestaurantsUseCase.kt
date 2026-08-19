package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.favourite.SyncFavouriteRestaurantsUseCase

class FakeSyncFavouriteRestaurantsUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    SyncFavouriteRestaurantsUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<Unit> {
        invokeCount++
        return result
    }
}
