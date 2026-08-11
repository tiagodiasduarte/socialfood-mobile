package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection

interface AddHomeSectionItemUseCase {
    suspend operator fun invoke(
        sectionId: String,
        itemId: String,
        itemType: HomeItemType,
        position: Int,
    ): Result<HomeSection>
}
