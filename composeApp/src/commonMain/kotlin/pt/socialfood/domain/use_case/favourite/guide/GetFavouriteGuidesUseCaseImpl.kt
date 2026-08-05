package pt.socialfood.domain.use_case.favourite.guide

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class GetFavouriteGuidesUseCaseImpl(private val repository: FavouritesGuidesRepository) : GetFavouriteGuidesUseCase {
    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedFavouriteGuides> =
        repository.getFavouritesPaged(page, limit)
}
