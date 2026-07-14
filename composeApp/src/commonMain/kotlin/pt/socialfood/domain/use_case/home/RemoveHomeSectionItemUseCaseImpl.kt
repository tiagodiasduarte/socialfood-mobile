package pt.socialfood.domain.use_case.home

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.HomeRepository

class RemoveHomeSectionItemUseCaseImpl(
    private val repository: HomeRepository,
) : RemoveHomeSectionItemUseCase {
    override suspend operator fun invoke(sectionId: String, itemId: String): Result<Boolean> =
        repository.removeItem(sectionId, itemId)
}
