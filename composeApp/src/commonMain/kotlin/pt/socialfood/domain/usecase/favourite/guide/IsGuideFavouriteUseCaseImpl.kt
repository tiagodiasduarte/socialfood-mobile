package pt.socialfood.domain.usecase.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class IsGuideFavouriteUseCaseImpl(private val repository: FavouritesGuidesRepository) : IsGuideFavouriteUseCase {
    override suspend operator fun invoke(guideId: String): Result<Boolean> = repository.isFavourite(guideId)
}
