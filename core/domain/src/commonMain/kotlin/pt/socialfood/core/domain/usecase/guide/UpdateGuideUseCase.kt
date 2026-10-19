package pt.socialfood.core.domain.usecase.guide

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility

interface UpdateGuideUseCase {
    suspend operator fun invoke(
        id: String,
        title: String,
        description: String,
        restaurantIds: List<String>,
        visibility: GuideVisibility,
    ): Result<Guide>
}
