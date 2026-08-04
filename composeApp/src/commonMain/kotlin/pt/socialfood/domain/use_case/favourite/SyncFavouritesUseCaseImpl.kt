package pt.socialfood.domain.use_case.favourite

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class SyncFavouritesUseCaseImpl(
    private val repository: FavouritesGuidesRepository,
) : SyncFavouritesUseCase {
    override suspend operator fun invoke(): Result<Unit> = repository.syncFavourites()
}
