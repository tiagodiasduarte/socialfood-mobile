package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface GetGuideByIdUseCase {
    suspend operator fun invoke(id: String): Result<Guide>
}
