package pt.socialfood.domain.use_case.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface GetGuidesUseCase {
    suspend operator fun invoke(): Result<List<Guide>>
}