package pt.socialfood.core.testing.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.usecase.guide.CreateGuideUseCase
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility

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
