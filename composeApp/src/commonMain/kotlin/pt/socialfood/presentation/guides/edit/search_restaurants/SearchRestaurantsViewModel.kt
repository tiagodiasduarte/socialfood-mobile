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
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByPlaceIdUseCase
import pt.socialfood.domain.use_case.restaurant.RestaurantEnrichmentPolling

class SearchRestaurantsViewModel(
    private val searchPlaces: SearchPlacesUseCase,
    private val getRestaurantByPlaceId: GetRestaurantByPlaceIdUseCase,
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
        if (_isImportingRestaurant.value) return

        addRestaurantJob = viewModelScope.launch {
            _isImportingRestaurant.value = true
            when (addRestaurantByPlaceId(placeId)) {
                is Result.Success -> {
                    val restaurant = awaitEnrichedRestaurant(placeId)
                    if (restaurant != null) {
                        _events.emit(UiEvent.RestaurantAdded(restaurant))
                    }
                    // If polling errors out or hits the cap while still enriching, we
                    // simply close the dialog without adding anything — the user can
                    // retry by tapping the place again.
                }

                is Result.Error -> Unit
            }
            _isImportingRestaurant.value = false
        }
    }

    private suspend fun awaitEnrichedRestaurant(placeId: String): Restaurant? {
        repeat(RestaurantEnrichmentPolling.MAX_POLL_ATTEMPTS) {
            when (val result = getRestaurantByPlaceId(placeId)) {
                is Result.Success -> if (!result.data.enriching) return result.data
                is Result.Error -> return null
            }
            delay(RestaurantEnrichmentPolling.POLL_INTERVAL_MS)
        }
        return null
    }

    override fun onCleared() {
        addRestaurantJob?.cancel()
        super.onCleared()
    }

    sealed class UiEvent {
        data class RestaurantAdded(val restaurant: Restaurant) : UiEvent()
    }
}
