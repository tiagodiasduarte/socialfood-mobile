package pt.socialfood.domain.use_case.favourite

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouritesRepository

class UnmarkGuideFavouriteUseCaseImpl(
    private val repository: FavouritesRepository,
) : UnmarkGuideFavouriteUseCase {
    override suspend operator fun invoke(guideId: String): Result<Unit> = repository.unmarkFavourite(guideId)
}
