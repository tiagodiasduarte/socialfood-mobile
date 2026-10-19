package pt.socialfood.core.domain.usecase.guide

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Guide
import pt.socialfood.core.model.GuideVisibility

interface CreateGuideUseCase {
    suspend operator fun invoke(
        title: String,
        description: String,
        visibility: GuideVisibility,
        restaurantIds: List<String>? = null,
    ): Result<Guide>
}
