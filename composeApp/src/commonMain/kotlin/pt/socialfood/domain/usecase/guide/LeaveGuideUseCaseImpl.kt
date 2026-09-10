package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.GuidesRepository

class LeaveGuideUseCaseImpl(private val repository: GuidesRepository) : LeaveGuideUseCase {
    override suspend operator fun invoke(guideId: String): Result<Boolean> = repository.leaveGuide(guideId)
}
