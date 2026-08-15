package pt.socialfood.presentation.guide.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.User
import pt.socialfood.domain.usecase.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.favourite.guide.ObserveFavouriteGuideIdsUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.usecase.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

const val ALL_GUIDES_TAB = 0
const val MY_GUIDES_TAB = 1

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesViewModel(
    private val getGuidesPaging: GetGuidesPagingUseCase,
    private val markGuideFavourite: MarkGuideFavouriteUseCase,
    private val unmarkGuideFavourite: UnmarkGuideFavouriteUseCase,
    observeUser: ObserveUserUseCase,
    observeFavouriteGuideIds: ObserveFavouriteGuideIdsUseCase,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(ALL_GUIDES_TAB)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val user: StateFlow<User?> = observeUser()
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val guides: Flow<PagingData<Guide>> = combine(_selectedTab, observeUser()) { tab, user ->
        if (tab == MY_GUIDES_TAB) user?.id else null
    }
        .distinctUntilChanged()
        .flatMapLatest { getGuidesPaging(it) }
        .cachedIn(viewModelScope)

    val favouriteGuideIds: StateFlow<Set<String>> = observeFavouriteGuideIds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = emptySet(),
        )

    fun onTabSelected(tab: Int) {
        _selectedTab.value = tab
    }

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
