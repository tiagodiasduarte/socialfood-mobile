package pt.socialfood.domain.usecase.favourite

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class SyncFavouriteRestaurantsUseCaseImpl(private val repository: FavouriteRestaurantsRepository) :
    SyncFavouriteRestaurantsUseCase {
    override suspend operator fun invoke(): Result<Unit> = repository.syncFavourites()
}
