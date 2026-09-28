package pt.socialfood.domain.usecase.favourite

import pt.socialfood.core.Result

interface SyncFavouriteRestaurantsUseCase {
    suspend operator fun invoke(): Result<Unit>
}
