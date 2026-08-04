package pt.socialfood.domain.use_case.favourite.guide

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class ObserveFavouriteGuideIdsUseCaseImpl(
    private val repository: FavouritesGuidesRepository,
) : ObserveFavouriteGuideIdsUseCase {
    override operator fun invoke(): Flow<Set<String>> = repository.observeFavouriteGuideIds()
}
