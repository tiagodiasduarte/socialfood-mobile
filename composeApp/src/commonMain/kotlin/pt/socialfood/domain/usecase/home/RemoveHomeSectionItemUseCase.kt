package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result

interface RemoveHomeSectionItemUseCase {
    suspend operator fun invoke(sectionId: String, itemId: String): Result<Boolean>
}
