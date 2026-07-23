package pt.socialfood.domain.use_case.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide

interface MarkGuideFavouriteUseCase {
    suspend operator fun invoke(guide: Guide): Result<Unit>
}
