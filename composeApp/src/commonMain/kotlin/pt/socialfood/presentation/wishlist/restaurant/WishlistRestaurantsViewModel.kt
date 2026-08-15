package pt.socialfood.presentation.wishlist.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.usecase.wishlist.restaurant.GetWishlistRestaurantsUseCase
import pt.socialfood.domain.usecase.wishlist.restaurant.UnmarkRestaurantWishlistUseCase
import pt.socialfood.presentation.error.toErrorCode

private const val PAGE_SIZE = 20

class WishlistRestaurantsViewModel(
    private val getWishlistRestaurants: GetWishlistRestaurantsUseCase,
    private val unmarkRestaurantWishlist: UnmarkRestaurantWishlistUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<WishlistRestaurantsUiState>(WishlistRestaurantsUiState.Loading)
    val state: StateFlow<WishlistRestaurantsUiState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 1

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = WishlistRestaurantsUiState.Loading
            currentPage = 1
            when (val result = getWishlistRestaurants(page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = WishlistRestaurantsUiState.Loaded(
                        restaurants = result.data.wishlist.map { it.restaurant },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Failure -> _state.value = WishlistRestaurantsUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            when (val result = getWishlistRestaurants(page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = WishlistRestaurantsUiState.Loaded(
                        restaurants = result.data.wishlist.map { it.restaurant },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Failure -> _state.value = WishlistRestaurantsUiState.Error(result.error.toErrorCode())
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val current = _state.value as? WishlistRestaurantsUiState.Loaded ?: return
        if (!current.hasMore || current.isLoadingMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMore = true)
            val nextPage = currentPage + 1
            when (val result = getWishlistRestaurants(page = nextPage, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = current.copy(
                        restaurants = current.restaurants + result.data.wishlist.map { it.restaurant },
                        hasMore = result.data.hasMore,
                        isLoadingMore = false,
                    )
                }
                is Result.Failure -> _state.value = current.copy(isLoadingMore = false)
            }
        }
    }

    fun removeFromWishlist(restaurantId: String) {
        val current = _state.value as? WishlistRestaurantsUiState.Loaded ?: return
        val removedRestaurant = current.restaurants.find { it.id == restaurantId } ?: return
        _state.value = current.copy(restaurants = current.restaurants.filterNot { it.id == restaurantId })

        viewModelScope.launch {
            val result = unmarkRestaurantWishlist(restaurantId)
            if (result is Result.Failure) {
                val stateNow = _state.value as? WishlistRestaurantsUiState.Loaded ?: return@launch
                _state.value = stateNow.copy(restaurants = listOf(removedRestaurant) + stateNow.restaurants)
            }
        }
    }
}
