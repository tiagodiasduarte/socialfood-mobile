package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.GuidesRepository

class JoinGuideUseCaseImpl(private val repository: GuidesRepository) : JoinGuideUseCase {
    override suspend operator fun invoke(guideId: String): Result<Guide> = repository.joinGuide(guideId)
}
