package pt.socialfood.domain.use_case.favourite.guide

import pt.socialfood.core.Result

interface UnmarkGuideFavouriteUseCase {
    suspend operator fun invoke(guideId: String): Result<Unit>
}
