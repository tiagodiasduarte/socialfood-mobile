package pt.socialfood.domain.usecase.favourite.guide

import pt.socialfood.core.Result

interface IsGuideFavouriteUseCase {
    suspend operator fun invoke(guideId: String): Result<Boolean>
}
