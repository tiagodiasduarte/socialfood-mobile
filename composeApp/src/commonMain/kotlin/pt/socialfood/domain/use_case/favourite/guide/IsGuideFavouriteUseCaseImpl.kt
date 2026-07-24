package pt.socialfood.domain.use_case.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouritesRepository

class IsGuideFavouriteUseCaseImpl(
    private val repository: FavouritesRepository,
) : IsGuideFavouriteUseCase {
    override suspend operator fun invoke(guideId: String): Result<Boolean> = repository.isFavourite(guideId)
}
