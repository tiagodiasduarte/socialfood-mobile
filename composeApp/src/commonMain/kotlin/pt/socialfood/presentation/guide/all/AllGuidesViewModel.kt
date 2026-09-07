package pt.socialfood.presentation.guide.all

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

class AllGuidesViewModel(
    getGuidesPaging: GetGuidesPagingUseCase,
    private val markGuideFavourite: MarkGuideFavouriteUseCase,
    private val unmarkGuideFavourite: UnmarkGuideFavouriteUseCase,
    observeUser: ObserveUserUseCase,
    observeFavouriteGuideIds: ObserveFavouriteGuideIdsUseCase,
) : ViewModel() {

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val guides: Flow<PagingData<Guide>> = getGuidesPaging(userId = null).cachedIn(viewModelScope)

    val favouriteGuideIds: StateFlow<Set<String>> = observeFavouriteGuideIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet(),
        )

    fun onToggleGuideFavourite(guide: Guide) {
        viewModelScope.launch {
            if (guide.id in favouriteGuideIds.value) {
                unmarkGuideFavourite(guide.id)
            } else {
                markGuideFavourite(guide)
            }
        }
    }
}
