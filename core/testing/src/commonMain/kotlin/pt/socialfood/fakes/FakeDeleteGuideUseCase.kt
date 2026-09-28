package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.guide.DeleteGuideUseCase

class FakeDeleteGuideUseCase(private val result: Result<Boolean>) : DeleteGuideUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(id: String): Result<Boolean> {
        invokeCount++
        return result
    }
}
