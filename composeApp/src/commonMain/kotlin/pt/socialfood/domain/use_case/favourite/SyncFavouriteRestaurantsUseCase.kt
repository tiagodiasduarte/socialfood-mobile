package pt.socialfood.domain.use_case.favourite

import pt.socialfood.core.Result

interface SyncFavouriteRestaurantsUseCase {
    suspend operator fun invoke(): Result<Unit>
}
