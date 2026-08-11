package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.GuidesRepository

class GetGuidesUseCaseImpl(
    private val repository: GuidesRepository,
) : GetGuidesUseCase {
    override suspend operator fun invoke(): Result<List<Guide>> = repository.findGuides()
}
