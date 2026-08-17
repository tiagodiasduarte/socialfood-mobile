package pt.socialfood.domain.usecase.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface GetGuidesUseCase {
    suspend operator fun invoke(): Result<List<Guide>>
}
