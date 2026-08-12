package pt.socialfood.domain.usecase.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class MarkGuideFavouriteUseCaseImpl(private val repository: FavouritesGuidesRepository) : MarkGuideFavouriteUseCase {
    override suspend operator fun invoke(guide: Guide): Result<Unit> = repository.markFavourite(guide)
}
