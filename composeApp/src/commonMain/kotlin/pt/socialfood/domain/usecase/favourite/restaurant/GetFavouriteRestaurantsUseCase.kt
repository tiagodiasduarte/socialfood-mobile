package pt.socialfood.domain.usecase.favourite.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedFavouriteRestaurants

interface GetFavouriteRestaurantsUseCase {
    suspend operator fun invoke(page: Int, limit: Int): Result<PagedFavouriteRestaurants>
}
