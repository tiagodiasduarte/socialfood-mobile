package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.usecase.guide.UpdateGuideUseCase

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
