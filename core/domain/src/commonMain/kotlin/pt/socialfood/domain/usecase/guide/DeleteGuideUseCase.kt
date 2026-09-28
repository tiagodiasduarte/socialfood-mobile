package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result

interface DeleteGuideUseCase {
    suspend operator fun invoke(id: String): Result<Boolean>
}
