package pt.socialfood.domain.usecase.home

import pt.socialfood.core.Result

interface DeleteHomeSectionUseCase {
    suspend operator fun invoke(id: String): Result<Boolean>
}
