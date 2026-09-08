package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface FindGuideBySharedCodeUseCase {
    suspend operator fun invoke(code: String): Result<Guide>
}
