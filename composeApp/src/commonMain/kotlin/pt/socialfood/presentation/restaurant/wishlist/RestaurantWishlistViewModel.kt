package pt.socialfood.presentation.restaurant.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetRestaurantVisitStatusUseCase
import pt.socialfood.domain.usecase.restaurantvisitstatus.UnmarkRestaurantVisitStatusUseCase
import pt.socialfood.presentation.error.toErrorCode

private const val PAGE_SIZE = 20
private val STATUS = VisitStatus.WISH

class RestaurantWishlistViewModel(
    private val getRestaurantVisitStatus: GetRestaurantVisitStatusUseCase,
    private val unmarkRestaurantVisitStatus: UnmarkRestaurantVisitStatusUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<RestaurantWishlistUiState>(RestaurantWishlistUiState.Loading)
    val state: StateFlow<RestaurantWishlistUiState> = _state.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var currentPage = 1

    init {
        loadFirstPage()
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = RestaurantWishlistUiState.Loading
            currentPage = 1
            when (val result = getRestaurantVisitStatus(status = STATUS, page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = RestaurantWishlistUiState.Loaded(
                        restaurants = result.data.visits.map { it.restaurant },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Failure -> _state.value = RestaurantWishlistUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            when (val result = getRestaurantVisitStatus(status = STATUS, page = 1, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = RestaurantWishlistUiState.Loaded(
                        restaurants = result.data.visits.map { it.restaurant },
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Failure -> _state.value = RestaurantWishlistUiState.Error(result.error.toErrorCode())
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val current = _state.value as? RestaurantWishlistUiState.Loaded ?: return
        if (!current.hasMore || current.isLoadingMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMore = true)
            val nextPage = currentPage + 1
            when (val result = getRestaurantVisitStatus(status = STATUS, page = nextPage, limit = PAGE_SIZE)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = current.copy(
                        restaurants = current.restaurants + result.data.visits.map { it.restaurant },
                        hasMore = result.data.hasMore,
                        isLoadingMore = false,
                    )
                }
                is Result.Failure -> _state.value = current.copy(isLoadingMore = false)
            }
        }
    }

    fun removeFromWishlist(restaurantId: String) {
        val current = _state.value as? RestaurantWishlistUiState.Loaded ?: return
        val removedRestaurant = current.restaurants.find { it.id == restaurantId } ?: return
        _state.value = current.copy(restaurants = current.restaurants.filterNot { it.id == restaurantId })

        viewModelScope.launch {
            val result = unmarkRestaurantVisitStatus(restaurantId, STATUS)
            if (result is Result.Failure) {
                val stateNow = _state.value as? RestaurantWishlistUiState.Loaded ?: return@launch
                _state.value = stateNow.copy(restaurants = listOf(removedRestaurant) + stateNow.restaurants)
            }
        }
    }
}
