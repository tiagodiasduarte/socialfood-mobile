package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.usecase.guide.GetGuideBySharedCodeUseCase
import pt.socialfood.random.nextGuide
import kotlin.random.Random

class FakeGetGuideBySharedCodeUseCase(private val result: Result<Guide> = Result.Success(Random.nextGuide())) :
    GetGuideBySharedCodeUseCase {
    var invokeCount: Int = 0
        private set
    var lastCode: String? = null
        private set

    override suspend operator fun invoke(code: String): Result<Guide> {
        invokeCount++
        lastCode = code
        return result
    }
}
