package pt.socialfood.domain.use_case.favourite.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class GetFavouriteRestaurantsUseCaseImpl(
    private val repository: FavouriteRestaurantsRepository,
) : GetFavouriteRestaurantsUseCase {
    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedFavouriteRestaurants> =
        repository.getFavouritesPaged(page, limit)
}
