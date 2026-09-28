package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface GetGuideBySharedCodeUseCase {
    suspend operator fun invoke(code: String): Result<Guide>
}
