package pt.socialfood.domain.usecase.favourite

import pt.socialfood.core.Result

interface SyncFavouritesUseCase {
    suspend operator fun invoke(): Result<Unit>
}
