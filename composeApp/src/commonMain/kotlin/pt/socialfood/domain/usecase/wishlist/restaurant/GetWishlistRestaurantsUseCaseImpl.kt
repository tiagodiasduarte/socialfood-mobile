package pt.socialfood.domain.usecase.wishlist.restaurant

import pt.socialfood.core.Result
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.repository.WishlistRestaurantsRepository

class GetWishlistRestaurantsUseCaseImpl(private val repository: WishlistRestaurantsRepository) :
    GetWishlistRestaurantsUseCase {
    override suspend operator fun invoke(page: Int, limit: Int): Result<PagedWishlistRestaurants> =
        repository.getWishlistPaged(page, limit)
}
