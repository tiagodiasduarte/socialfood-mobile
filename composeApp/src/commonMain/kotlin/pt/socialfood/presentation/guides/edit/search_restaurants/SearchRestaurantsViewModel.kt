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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.use_case.SearchPlacesUseCase
import pt.socialfood.domain.use_case.restaurant.AddRestaurantByPlaceIdUseCase
import pt.socialfood.domain.use_case.restaurant.AwaitEnrichedRestaurantByPlaceIdUseCase

class SearchRestaurantsViewModel(
    private val searchPlaces: SearchPlacesUseCase,
    private val awaitEnrichedRestaurantByPlaceId: AwaitEnrichedRestaurantByPlaceIdUseCase,
    private val addRestaurantByPlaceId: AddRestaurantByPlaceIdUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<SearchRestaurantsUiState>(SearchRestaurantsUiState.Loaded(emptyList()))
    val state: StateFlow<SearchRestaurantsUiState> = _state

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    // Drives the "Importing restaurant" dialog while addByPlaceId + the enrichment poll
    // that follows it are in flight.
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
        // Set synchronously, not inside the launched coroutine below: with a lazy
        // dispatcher the coroutine body doesn't run immediately, which would leave a
        // window where a rapid second call sails past this guard before the first
        // coroutine ever gets to flip the flag itself.
        if (_isImportingRestaurant.value) return
        _isImportingRestaurant.value = true

        addRestaurantJob = viewModelScope.launch {
            when (addRestaurantByPlaceId(placeId)) {
                is Result.Success -> {
                    // Suspends until the repository reports the restaurant is fully
                    // enriched, or gives up after its own poll cap — either way, a
                    // Restaurant reaching this ViewModel is never a still-enriching stub.
                    when (val result = awaitEnrichedRestaurantByPlaceId(placeId)) {
                        is Result.Success -> _events.emit(UiEvent.RestaurantAdded(result.data))
                        // Timed out or errored mid-poll: close the dialog without adding
                        // anything, the user can retry by tapping the place again.
                        is Result.Error -> Unit
                    }
                }

                is Result.Error -> Unit
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
}
