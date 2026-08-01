package pt.socialfood.domain.use_case.favourite.guide

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.repository.FavouritesRepository

class ObserveFavouriteGuideIdsUseCaseImpl(
    private val repository: FavouritesRepository,
) : ObserveFavouriteGuideIdsUseCase {
    override operator fun invoke(): Flow<Set<String>> = repository.observeFavouriteGuideIds()
}
