package pt.socialfood.presentation.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.use_case.guide.FindGuidesUseCase
import pt.socialfood.domain.use_case.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase

private const val PAGE_SIZE = 20

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesViewModel(
    private val findGuides: FindGuidesUseCase,
    private val getUserMe: GetUserMeUseCase,
    private val getGuidesPaging: GetGuidesPagingUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<GuidesUiState>(GuidesUiState.Loading)
    val state: StateFlow<GuidesUiState> = _state

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var currentPage = 1
    private var currentUserId: String? = null

    // Legacy tab-selection state feeding the old GuidesUiState-based loadFirstPage()/refresh()/
    // loadMore() path. Kept separate (and separately named) from the new `_selectedTab` below so
    // the old and new code paths can coexist until the old path is deleted in APPS-20-4. Named
    // distinctly (rather than `selectedTab`) only because Kotlin disallows two properties sharing
    // one name in the same class — the new public `selectedTab: StateFlow<Int>` needs that name.
    private var legacySelectedTab = 0

    // New tab-selection / current-user-id state feeding `guides`. Deliberately duplicated
    // alongside the legacy state above rather than reusing it directly — see APPS-20-3 plan.
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)

    val guides: Flow<PagingData<Guide>> = combine(_selectedTab, _currentUserId) { tab, userId ->
        if (tab == 1) userId else null
    }.distinctUntilChanged().flatMapLatest { getGuidesPaging(it) }.cachedIn(viewModelScope)

    init {
        loadFirstPage()
        viewModelScope.launch {
            val userResult = getUserMe()
            if (userResult is Result.Success) {
                _currentUserId.value = userResult.data.id
            }
        }
    }

    fun onTabSelected(tab: Int) {
        if (legacySelectedTab == tab) return
        legacySelectedTab = tab
        _selectedTab.value = tab
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = GuidesUiState.Loading
            currentPage = 1

            val userResult = getUserMe()
            if (userResult is Result.Success) {
                currentUserId = userResult.data.id
            }

            val userId = if(legacySelectedTab == 1) currentUserId else null
            val guidesDeferred = async { findGuides(userId = userId, page = 1, limit = PAGE_SIZE) }

            when (val result = guidesDeferred.await()) {
                is Result.Success -> {
                    currentPage = result.data.page
                    val guides = result.data.guides
                    _state.value = GuidesUiState.Loaded(
                        guides = guides,
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = GuidesUiState.Error
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1

            val userId = if(legacySelectedTab == 1) currentUserId else null
            when (val result = findGuides(userId = userId, page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    val guides = result.data.guides
                    _state.value = GuidesUiState.Loaded(
                        guides = guides,
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = GuidesUiState.Error
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val current = _state.value as? GuidesUiState.Loaded ?: return
        if (!current.hasMore || current.isLoadingMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMore = true)
            val nextPage = currentPage + 1

            val userId = if(legacySelectedTab == 1) currentUserId else null
            when (val result = findGuides(userId = userId, page = nextPage, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    val allGuides = current.guides + result.data.guides
                    _state.value = current.copy(
                        guides = allGuides,
                        hasMore = result.data.hasMore,
                        isLoadingMore = false,
                    )
                }
                is Result.Error -> _state.value = current.copy(isLoadingMore = false)
            }
        }
    }
}
