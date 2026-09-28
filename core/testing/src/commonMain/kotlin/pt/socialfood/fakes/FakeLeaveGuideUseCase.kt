package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.guide.LeaveGuideUseCase

class FakeLeaveGuideUseCase(private val result: Result<Boolean> = Result.Success(true)) : LeaveGuideUseCase {
    var invokeCount: Int = 0
        private set
    var lastGuideId: String? = null
        private set

    override suspend fun invoke(guideId: String): Result<Boolean> {
        invokeCount++
        lastGuideId = guideId
        return result
    }
}
