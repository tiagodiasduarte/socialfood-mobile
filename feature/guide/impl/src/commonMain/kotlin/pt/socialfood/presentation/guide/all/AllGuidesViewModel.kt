package pt.socialfood.presentation.guide.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.User
import pt.socialfood.domain.repository.FavouritesGuidesRepository
import pt.socialfood.domain.repository.GuidesRepository
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class AllGuidesViewModel(
    guidesRepository: GuidesRepository,
    private val favouritesGuidesRepository: FavouritesGuidesRepository,
    observeUser: ObserveUserUseCase,
) : ViewModel() {

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val guides: Flow<PagingData<Guide>> = user
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { guidesRepository.findGuidesPagingFlow() }
        .cachedIn(viewModelScope)

    val favouriteGuideIds: StateFlow<Set<String>> = favouritesGuidesRepository.observeFavouriteGuideIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet(),
        )

    fun onToggleGuideFavourite(guide: Guide) {
        viewModelScope.launch {
            if (guide.id in favouriteGuideIds.value) {
                favouritesGuidesRepository.unmark(guide.id)
            } else {
                favouritesGuidesRepository.mark(guide)
            }
        }
    }
}
