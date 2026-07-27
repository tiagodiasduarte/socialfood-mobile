package pt.socialfood.presentation.guide.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.use_case.guide.FindGuidesUseCase
import pt.socialfood.domain.use_case.guide.GetGuidesPagingUseCase
import pt.socialfood.domain.use_case.user.ObserveUserUseCase

const val ALL_GUIDES_TAB = 0
const val MY_GUIDES_TAB = 1
private const val PAGE_SIZE = 20
private const val SEARCH_DEBOUNCE_MS = 300L

@OptIn(ExperimentalCoroutinesApi::class)
class GuidesViewModel(
    private val getGuidesPaging: GetGuidesPagingUseCase,
    private val findGuides: FindGuidesUseCase,
    private val observeUser: ObserveUserUseCase,
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(ALL_GUIDES_TAB)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val currentUser = observeUser().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val guides: Flow<PagingData<Guide>> = combine(_selectedTab, currentUser) { tab, user ->
        if (tab == MY_GUIDES_TAB) user?.id else null
    }.distinctUntilChanged().flatMapLatest { getGuidesPaging(it) }.cachedIn(viewModelScope)

    private val _state = MutableStateFlow<GuidesUiState>(GuidesUiState.Loading)
    val state: StateFlow<GuidesUiState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private var currentQuery: String? = null

    init {
        // The blank-query browse list is served by `guides` (Paging + cache) instead, so this
        // collector only ever needs to (re)load a search result page — it's a no-op once the
        // query is cleared back to blank.
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _searchQuery
                .debounce(SEARCH_DEBOUNCE_MS)
                .collectLatest { query ->
                    if (query.isBlank()) return@collectLatest
                    currentQuery = query
                    loadFirstPage()
                }
        }
    }

    fun onTabSelected(tab: Int) {
        _selectedTab.value = tab
        if (_searchQuery.value.isNotBlank()) {
            loadFirstPage()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            when (val result = findGuides(page = 1, limit = PAGE_SIZE, query = currentQuery, userId = scopedUserId())) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = GuidesUiState.Loaded(
                        guides = result.data.guides,
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
            when (val result = findGuides(page = nextPage, limit = PAGE_SIZE, query = currentQuery, userId = scopedUserId())) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = current.copy(
                        guides = current.guides + result.data.guides,
                        hasMore = result.data.hasMore,
                        isLoadingMore = false,
                    )
                }
                is Result.Error -> {
                    _state.value = current.copy(isLoadingMore = false)
                }
            }
        }
    }

    private fun scopedUserId(): String? =
        if (_selectedTab.value == MY_GUIDES_TAB) currentUser.value?.id else null

    private fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = GuidesUiState.Loading
            currentPage = 1
            when (val result = findGuides(page = 1, limit = PAGE_SIZE, query = currentQuery, userId = scopedUserId())) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = GuidesUiState.Loaded(
                        guides = result.data.guides,
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = GuidesUiState.Error
            }
        }
    }
}
