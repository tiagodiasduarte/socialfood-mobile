package pt.socialfood.presentation.favourite.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.favourite.guide.GetFavouriteGuidesUseCase
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.error.toErrorCode

private const val PAGE_SIZE = 20

class FavouriteGuidesViewModel(
    private val getFavouriteGuides: GetFavouriteGuidesUseCase,
    private val unmarkGuideFavourite: UnmarkGuideFavouriteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<FavouriteGuidesUiState>(FavouriteGuidesUiState.Loading)
    val state: StateFlow<FavouriteGuidesUiState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 1

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = FavouriteGuidesUiState.Loading
            currentPage = 1
            when (val result = getFavouriteGuides(page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = FavouriteGuidesUiState.Loaded(
                        guides = result.data.favourites.map { it.guide },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Failure -> _state.value = FavouriteGuidesUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            when (val result = getFavouriteGuides(page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = FavouriteGuidesUiState.Loaded(
                        guides = result.data.favourites.map { it.guide },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Failure -> _state.value = FavouriteGuidesUiState.Error(result.error.toErrorCode())
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val current = _state.value as? FavouriteGuidesUiState.Loaded ?: return
        if (!current.hasMore || current.isLoadingMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMore = true)
            val nextPage = currentPage + 1
            when (val result = getFavouriteGuides(page = nextPage, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = current.copy(
                        guides = current.guides + result.data.favourites.map { it.guide },
                        hasMore = result.data.hasMore,
                        isLoadingMore = false,
                    )
                }
                is Result.Failure -> _state.value = current.copy(isLoadingMore = false)
            }
        }
    }

    fun removeFavourite(guideId: String) {
        val current = _state.value as? FavouriteGuidesUiState.Loaded ?: return
        val removedGuide = current.guides.find { it.id == guideId } ?: return
        _state.value = current.copy(guides = current.guides.filterNot { it.id == guideId })

        viewModelScope.launch {
            val result = unmarkGuideFavourite(guideId)
            if (result is Result.Failure) {
                val stateNow = _state.value as? FavouriteGuidesUiState.Loaded ?: return@launch
                _state.value = stateNow.copy(guides = listOf(removedGuide) + stateNow.guides)
            }
        }
    }
}
