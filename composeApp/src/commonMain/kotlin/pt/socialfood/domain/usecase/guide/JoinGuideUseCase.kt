package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface JoinGuideUseCase {
    suspend operator fun invoke(guideId: String): Result<Guide>
}
