package pt.socialfood.presentation.restaurant.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByIdUseCase


class RestaurantDetailViewModel(
    private val getRestaurantById: GetRestaurantByIdUseCase,
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
            when (val result = getRestaurantById(restaurantId)) {
                is Result.Success -> _state.value = RestaurantDetailUiState.Loaded(result.data)
                is Result.Error -> _state.value = RestaurantDetailUiState.Error
            }
        }
    }
}
