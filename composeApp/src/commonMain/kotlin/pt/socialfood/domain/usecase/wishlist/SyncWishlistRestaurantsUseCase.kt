package pt.socialfood.domain.usecase.wishlist

import pt.socialfood.core.Result

interface SyncWishlistRestaurantsUseCase {
    suspend operator fun invoke(): Result<Unit>
}
