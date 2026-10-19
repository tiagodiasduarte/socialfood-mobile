package pt.socialfood.core.testing.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.usecase.guide.UpdateGuideUseCase
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility

class FakeUpdateGuideUseCase(private val result: Result<Guide>) : UpdateGuideUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(
        id: String,
        title: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide> {
        invokeCount++
        return result
    }
}
