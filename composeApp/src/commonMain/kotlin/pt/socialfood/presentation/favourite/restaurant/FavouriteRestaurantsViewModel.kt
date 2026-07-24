package pt.socialfood.presentation.favourite.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.favourite.restaurant.GetFavouriteRestaurantsUseCase

private const val PAGE_SIZE = 20

class FavouriteRestaurantsViewModel(
    private val getFavouriteRestaurants: GetFavouriteRestaurantsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<FavouriteRestaurantsUiState>(FavouriteRestaurantsUiState.Loading)
    val state: StateFlow<FavouriteRestaurantsUiState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 1

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = FavouriteRestaurantsUiState.Loading
            currentPage = 1
            when (val result = getFavouriteRestaurants(page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = FavouriteRestaurantsUiState.Loaded(
                        restaurants = result.data.favourites.map { it.restaurant },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = FavouriteRestaurantsUiState.Error
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            when (val result = getFavouriteRestaurants(page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = FavouriteRestaurantsUiState.Loaded(
                        restaurants = result.data.favourites.map { it.restaurant },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = FavouriteRestaurantsUiState.Error
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val current = _state.value as? FavouriteRestaurantsUiState.Loaded ?: return
        if (!current.hasMore || current.isLoadingMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMore = true)
            val nextPage = currentPage + 1
            when (val result = getFavouriteRestaurants(page = nextPage, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = current.copy(
                        restaurants = current.restaurants + result.data.favourites.map { it.restaurant },
                        hasMore = result.data.hasMore,
                        isLoadingMore = false,
                    )
                }
                is Result.Error -> _state.value = current.copy(isLoadingMore = false)
            }
        }
    }
}
