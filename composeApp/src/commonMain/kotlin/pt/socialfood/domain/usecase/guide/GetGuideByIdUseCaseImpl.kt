package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.GuidesRepository

class GetGuideByIdUseCaseImpl(
    private val repository: GuidesRepository,
) : GetGuideByIdUseCase {
    override suspend operator fun invoke(id: String): Result<Guide> = repository.findById(id)
}
