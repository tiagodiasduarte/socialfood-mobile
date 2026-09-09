package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.GuidesRepository

class GetGuideBySharedCodeUseCaseImpl(private val repository: GuidesRepository) : GetGuideBySharedCodeUseCase {
    override suspend operator fun invoke(code: String): Result<Guide> = repository.findGuideBySharedCode(code)
}
