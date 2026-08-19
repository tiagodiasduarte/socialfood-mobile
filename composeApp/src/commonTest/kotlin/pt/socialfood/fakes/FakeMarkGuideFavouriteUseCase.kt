package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase

class FakeMarkGuideFavouriteUseCase(private val result: Result<Unit> = Result.Success(Unit)) :
    MarkGuideFavouriteUseCase {
    var invokeCount: Int = 0
        private set
    var lastGuide: Guide? = null
        private set

    override suspend fun invoke(guide: Guide): Result<Unit> {
        invokeCount++
        lastGuide = guide
        return result
    }
}
