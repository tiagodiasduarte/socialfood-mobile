package pt.socialfood.presentation.restaurant.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.favourite.restaurant.IsRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.MarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.UnmarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByIdUseCase


class RestaurantDetailViewModel(
    private val getRestaurantById: GetRestaurantByIdUseCase,
    private val isRestaurantFavourite: IsRestaurantFavouriteUseCase,
    private val markRestaurantFavourite: MarkRestaurantFavouriteUseCase,
    private val unmarkRestaurantFavourite: UnmarkRestaurantFavouriteUseCase,
    private val restaurantId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<RestaurantDetailUiState>(RestaurantDetailUiState.Loading)
    val state: StateFlow<RestaurantDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = RestaurantDetailUiState.Loading
            val restaurantDeferred = async { getRestaurantById(restaurantId) }
            val isFavouriteDeferred = async { isRestaurantFavourite(restaurantId) }
            val restaurantResult = restaurantDeferred.await()
            val isFavouriteResult = isFavouriteDeferred.await()
            _state.value = when (restaurantResult) {
                is Result.Success -> RestaurantDetailUiState.Loaded(
                    restaurant = restaurantResult.data,
                    isFavourite = (isFavouriteResult as? Result.Success)?.data ?: false,
                )
                is Result.Failure -> RestaurantDetailUiState.Error
            }
        }
    }

    fun toggleFavourite() {
        val current = _state.value as? RestaurantDetailUiState.Loaded ?: return
        val newIsFavourite = !current.isFavourite
        _state.value = current.copy(isFavourite = newIsFavourite)

        viewModelScope.launch {
            val result = if (newIsFavourite) {
                markRestaurantFavourite(current.restaurant)
            } else {
                unmarkRestaurantFavourite(current.restaurant.id)
            }
            if (result is Result.Failure) {
                val stateNow = _state.value as? RestaurantDetailUiState.Loaded ?: return@launch
                _state.value = stateNow.copy(isFavourite = !newIsFavourite)
            }
        }
    }
}
