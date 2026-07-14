package pt.socialfood.domain.use_case.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface GetGuideByIdUseCase {
    suspend operator fun invoke(id: String): Result<Guide>
}