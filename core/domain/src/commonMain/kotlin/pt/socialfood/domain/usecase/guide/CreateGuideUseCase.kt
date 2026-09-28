package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility

interface CreateGuideUseCase {
    suspend operator fun invoke(
        title: String,
        description: String,
        visibility: GuideVisibility,
        restaurantIds: List<String>? = null,
    ): Result<Guide>
}
