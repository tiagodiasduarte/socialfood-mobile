package pt.socialfood.domain.usecase.wishlist

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.WishlistRestaurantsRepository

class SyncWishlistRestaurantsUseCaseImpl(private val repository: WishlistRestaurantsRepository) :
    SyncWishlistRestaurantsUseCase {
    override suspend operator fun invoke(): Result<Unit> = repository.syncWishlist()
}
