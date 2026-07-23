package pt.socialfood.presentation.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.favourite.GetFavouriteGuidesUseCase

private const val PAGE_SIZE = 20

class FavouritesViewModel(
    private val getFavouriteGuides: GetFavouriteGuidesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<FavouritesUiState>(FavouritesUiState.Loading)
    val state: StateFlow<FavouritesUiState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 1

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = FavouritesUiState.Loading
            currentPage = 1
            when (val result = getFavouriteGuides(page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = FavouritesUiState.Loaded(
                        guides = result.data.favourites.map { it.guide },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = FavouritesUiState.Error
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
                    _state.value = FavouritesUiState.Loaded(
                        guides = result.data.favourites.map { it.guide },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = FavouritesUiState.Error
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val current = _state.value as? FavouritesUiState.Loaded ?: return
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
                is Result.Error -> _state.value = current.copy(isLoadingMore = false)
            }
        }
    }
}
