package pt.socialfood.domain.use_case.home

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.repository.HomeRepository

class AddHomeSectionItemUseCaseImpl(
    private val repository: HomeRepository,
) : AddHomeSectionItemUseCase {
    override suspend operator fun invoke(sectionId: String, itemId: String, itemType: HomeItemType, position: Int): Result<HomeSection> =
        repository.addItem(sectionId, itemId, itemType, position)
}
