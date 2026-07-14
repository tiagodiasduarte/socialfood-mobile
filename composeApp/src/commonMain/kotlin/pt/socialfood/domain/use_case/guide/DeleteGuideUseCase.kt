package pt.socialfood.domain.use_case.guide

import pt.socialfood.core.Result

interface DeleteGuideUseCase {
    suspend operator fun invoke(id: String): Result<Boolean>
}