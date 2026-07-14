package pt.socialfood.domain.use_case.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility

interface UpdateGuideUseCase {
    suspend operator fun invoke(
        id: String,
        title: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide>
}