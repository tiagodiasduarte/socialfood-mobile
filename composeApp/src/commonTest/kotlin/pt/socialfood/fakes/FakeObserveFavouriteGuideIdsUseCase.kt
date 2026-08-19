package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase

class FakeObserveFavouriteGuideIdsUseCase(initial: Set<String> = emptySet()) : ObserveFavouriteGuideIdsUseCase {
    private val ids = MutableStateFlow(initial)

    fun emit(ids: Set<String>) {
        this.ids.value = ids
    }

    override operator fun invoke(): Flow<Set<String>> = ids
}
