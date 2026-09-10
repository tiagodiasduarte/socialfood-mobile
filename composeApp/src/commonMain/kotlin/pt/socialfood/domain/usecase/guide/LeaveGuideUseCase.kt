package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result

interface LeaveGuideUseCase {
    suspend operator fun invoke(guideId: String): Result<Boolean>
}
