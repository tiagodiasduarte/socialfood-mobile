package pt.socialfood.presentation.guides.edit.search_restaurants

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.use_case.SearchPlacesUseCase
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByPlaceIdUseCase

class SearchRestaurantsViewModel(
    private val searchPlaces: SearchPlacesUseCase,
    private val getRestaurantByPlaceId: GetRestaurantByPlaceIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SearchRestaurantsUiState>(SearchRestaurantsUiState.Loaded(emptyList()))
    val state: StateFlow<SearchRestaurantsUiState> = _state

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    var searchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.value = SearchRestaurantsUiState.Loaded(emptyList())
            return
        }
        if (query.length < 3) {
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _state.value = SearchRestaurantsUiState.Loading
            when (val result = searchPlaces(query)) {
                is Result.Success -> _state.value = SearchRestaurantsUiState.Loaded(result.data)
                is Result.Error -> _state.value = SearchRestaurantsUiState.Error
            }
        }
    }

    fun onAddRestaurant(placeId: String) {
        viewModelScope.launch {
            when (val result = getRestaurantByPlaceId(placeId)) {
                is Result.Success -> {
                    _events.emit(UiEvent.RestaurantAdded(result.data))
                }

                is Result.Error -> {

                }
            }
        }
    }

    sealed class UiEvent {
        data class RestaurantAdded(val restaurant: Restaurant) : UiEvent()
    }
}
