package pt.socialfood.presentation.guides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.guide.FindGuidesUseCase
import pt.socialfood.domain.use_case.user.GetUserMeUseCase

private const val PAGE_SIZE = 20

class GuidesViewModel(
    private val findGuides: FindGuidesUseCase,
    private val getUserMe: GetUserMeUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<GuidesUiState>(GuidesUiState.Loading)
    val state: StateFlow<GuidesUiState> = _state

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private var currentPage = 1
    private var currentUserId: String? = null
    private var selectedTab = 0

    init {
        loadFirstPage()
    }

    fun onTabSelected(tab: Int) {
        if (selectedTab == tab) return
        selectedTab = tab
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

            val userId = if(selectedTab == 1) currentUserId else null
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

            val userId = if(selectedTab == 1) currentUserId else null
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

            val userId = if(selectedTab == 1) currentUserId else null
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
