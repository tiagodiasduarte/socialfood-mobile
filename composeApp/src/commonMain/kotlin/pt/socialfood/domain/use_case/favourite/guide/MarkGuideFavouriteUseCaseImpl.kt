package pt.socialfood.domain.use_case.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.FavouritesRepository

class MarkGuideFavouriteUseCaseImpl(
    private val repository: FavouritesRepository,
) : MarkGuideFavouriteUseCase {
    override suspend operator fun invoke(guide: Guide): Result<Unit> = repository.markFavourite(guide)
}
