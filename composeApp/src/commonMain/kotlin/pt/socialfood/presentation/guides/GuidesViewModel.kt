package pt.socialfood.presentation.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.use_case.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.use_case.user.ObserveUserUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesViewModel(
    private val getGuidesPaging: GetGuidesPagingUseCase,
    private val observeUser: ObserveUserUseCase,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    val guides: Flow<PagingData<Guide>> = combine(_selectedTab, observeUser()) { tab, user ->
        if (tab == 1) user?.id else null
    }.distinctUntilChanged().flatMapLatest { getGuidesPaging(it) }.cachedIn(viewModelScope)

    fun onTabSelected(tab: Int) {
        _selectedTab.value = tab
    }
}
