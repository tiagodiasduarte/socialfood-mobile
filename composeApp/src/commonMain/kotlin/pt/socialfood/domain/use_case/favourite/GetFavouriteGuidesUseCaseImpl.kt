package pt.socialfood.domain.use_case.favourite

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.repository.FavouritesRepository

class GetFavouriteGuidesUseCaseImpl(
    private val repository: FavouritesRepository,
) : GetFavouriteGuidesUseCase {
    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedFavouriteGuides> =
        repository.getFavouritesPaged(page, limit)
}
