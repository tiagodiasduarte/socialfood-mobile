package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.usecase.guide.JoinGuideUseCase
import pt.socialfood.random.nextGuide
import kotlin.random.Random

class FakeJoinGuideUseCase(private val result: Result<Guide> = Result.Success(Random.nextGuide())) : JoinGuideUseCase {
    var invokeCount: Int = 0
        private set
    var lastGuideId: String? = null
        private set

    override suspend operator fun invoke(guideId: String): Result<Guide> {
        invokeCount++
        lastGuideId = guideId
        return result
    }
}
