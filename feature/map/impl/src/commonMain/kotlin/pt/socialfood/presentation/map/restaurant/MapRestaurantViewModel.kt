package pt.socialfood.presentation.map.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.presentation.error.toErrorCode

class MapRestaurantViewModel(
    private val restaurantsRepository: RestaurantsRepository,
    private val restaurantId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<MapRestaurantUiState>(MapRestaurantUiState.Loading)
    val state: StateFlow<MapRestaurantUiState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = MapRestaurantUiState.Loading
            _state.value = when (val result = restaurantsRepository.findById(restaurantId)) {
                is Result.Success -> MapRestaurantUiState.Loaded(restaurant = result.data)
                is Result.Failure -> MapRestaurantUiState.Error(result.error.toErrorCode())
            }
        }
    }
}
