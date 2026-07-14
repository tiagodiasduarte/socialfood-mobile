package pt.socialfood.domain.use_case.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection

interface UpdateHomeSectionUseCase {
    suspend operator fun invoke(
        id: String,
        title: String,
        position: Int,
        isActive: Boolean,
        restaurantIds: List<String> = emptyList(),
        guideIds: List<String> = emptyList(),
    ): Result<HomeSection>
}
