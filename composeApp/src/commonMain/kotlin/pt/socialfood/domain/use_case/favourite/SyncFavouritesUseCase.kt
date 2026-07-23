package pt.socialfood.domain.use_case.favourite

import pt.socialfood.core.Result

interface SyncFavouritesUseCase {
    suspend operator fun invoke(): Result<Unit>
}
