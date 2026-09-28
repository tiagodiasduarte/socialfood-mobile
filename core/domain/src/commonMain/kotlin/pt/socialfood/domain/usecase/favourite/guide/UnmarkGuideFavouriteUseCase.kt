package pt.socialfood.domain.usecase.favourite.guide

import pt.socialfood.core.Result

interface UnmarkGuideFavouriteUseCase {
    suspend operator fun invoke(guideId: String): Result<Unit>
}
