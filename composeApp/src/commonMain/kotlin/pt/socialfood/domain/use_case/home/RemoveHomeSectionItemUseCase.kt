package pt.socialfood.domain.use_case.home

import pt.socialfood.core.Result

interface RemoveHomeSectionItemUseCase {
    suspend operator fun invoke(sectionId: String, itemId: String): Result<Boolean>
}
