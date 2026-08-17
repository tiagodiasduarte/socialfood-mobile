package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase

class FakeUnmarkGuideFavouriteUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    UnmarkGuideFavouriteUseCase {
    var invokeCount: Int = 0
        private set
    var lastGuideId: String? = null
        private set

    override suspend fun invoke(guideId: String): Result<Unit> {
        invokeCount++
        lastGuideId = guideId
        return result
    }
}
