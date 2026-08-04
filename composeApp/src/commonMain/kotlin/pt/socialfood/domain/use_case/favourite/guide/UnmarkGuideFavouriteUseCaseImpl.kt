package pt.socialfood.domain.use_case.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class UnmarkGuideFavouriteUseCaseImpl(
    private val repository: FavouritesGuidesRepository,
) : UnmarkGuideFavouriteUseCase {
    override suspend operator fun invoke(guideId: String): Result<Unit> = repository.unmarkFavourite(guideId)
}
