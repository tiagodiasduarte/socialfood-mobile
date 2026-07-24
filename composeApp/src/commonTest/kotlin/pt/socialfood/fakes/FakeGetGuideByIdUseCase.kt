package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.use_case.guide.GetGuideByIdUseCase

class FakeGetGuideByIdUseCase(
    private val result: Result<Guide>,
) : GetGuideByIdUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(id: String): Result<Guide> {
        invokeCount++
        return result
    }
}
