package pt.socialfood.domain.use_case.home

import pt.socialfood.core.Result

interface DeleteHomeSectionUseCase {
    suspend operator fun invoke(id: String): Result<Boolean>
}
