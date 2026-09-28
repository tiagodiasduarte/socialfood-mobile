package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.usecase.guide.CreateGuideUseCase

class FakeCreateGuideUseCase(private val result: Result<Guide>) : CreateGuideUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(
        title: String,
        description: String,
        visibility: GuideVisibility,
        restaurantIds: List<String>?,
    ): Result<Guide> {
        invokeCount++
        return result
    }
}
