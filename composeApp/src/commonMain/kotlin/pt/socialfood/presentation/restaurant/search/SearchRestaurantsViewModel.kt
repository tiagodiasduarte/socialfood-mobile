package pt.socialfood.presentation.restaurant.search

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.usecase.SearchPlacesUseCase
import pt.socialfood.domain.usecase.restaurant.AddRestaurantByPlaceIdUseCase
import pt.socialfood.domain.usecase.restaurant.AwaitEnrichedRestaurantByPlaceIdUseCase
import pt.socialfood.presentation.error.toErrorCode
import kotlin.time.Duration.Companion.milliseconds

class SearchRestaurantsViewModel(
    private val searchPlaces: SearchPlacesUseCase,
    private val awaitEnrichedRestaurantByPlaceId: AwaitEnrichedRestaurantByPlaceIdUseCase,
    private val addRestaurantByPlaceId: AddRestaurantByPlaceIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SearchRestaurantsUiState>(SearchRestaurantsUiState.Loaded(emptyList()))
    val state: StateFlow<SearchRestaurantsUiState> = _state

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    private val _isImportingRestaurant = MutableStateFlow(false)
    val isImportingRestaurant: StateFlow<Boolean> = _isImportingRestaurant.asStateFlow()

    var searchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null
    private var addRestaurantJob: Job? = null

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.value = SearchRestaurantsUiState.Loaded(emptyList())
            return
        }
        if (query.length < MIN_QUERY_LENGTH) {
            return
        }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _state.value = SearchRestaurantsUiState.Loading
            when (val result = searchPlaces(query)) {
                is Result.Success -> _state.value = SearchRestaurantsUiState.Loaded(result.data)
                is Result.Failure -> _state.value = SearchRestaurantsUiState.Error(result.error.toErrorCode())
            }
        }
    }

    fun onAddRestaurant(placeId: String) {
        if (_isImportingRestaurant.value) return
        _isImportingRestaurant.value = true

        addRestaurantJob = viewModelScope.launch {
            when (addRestaurantByPlaceId(placeId)) {
                is Result.Success -> {
                    when (val result = awaitEnrichedRestaurantByPlaceId(placeId)) {
                        is Result.Success -> _events.emit(UiEvent.RestaurantAdded(result.data))
                        is Result.Failure -> Unit
                    }
                }

                is Result.Failure -> Unit
            }
            _isImportingRestaurant.value = false
        }
    }

    override fun onCleared() {
        addRestaurantJob?.cancel()
        super.onCleared()
    }

    sealed class UiEvent {
        data class RestaurantAdded(val restaurant: Restaurant) : UiEvent()
    }

    companion object {
        private val SEARCH_DEBOUNCE_MS = 300.milliseconds
        private const val MIN_QUERY_LENGTH = 3
    }
}
