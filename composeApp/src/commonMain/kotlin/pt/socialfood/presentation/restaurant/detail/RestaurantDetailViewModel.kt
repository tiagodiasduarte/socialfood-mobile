package pt.socialfood.presentation.restaurant.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.restaurant.GetRestaurantByIdUseCase

internal const val ENRICHMENT_POLL_INTERVAL_MS = 2_000L
internal const val ENRICHMENT_POLL_MAX_ATTEMPTS = 10

class RestaurantDetailViewModel(
    private val getRestaurantById: GetRestaurantByIdUseCase,
    private val restaurantId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<RestaurantDetailUiState>(RestaurantDetailUiState.Loading)
    val state: StateFlow<RestaurantDetailUiState> = _state.asStateFlow()

    // Tracks the single in-flight load-then-poll sequence, so a retry (or onCleared) can
    // never leave a previous sequence's polling running alongside a new one.
    private var loadJob: Job? = null

    init {
        load()
    }

    fun load() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = RestaurantDetailUiState.Loading
            when (val result = getRestaurantById(restaurantId)) {
                is Result.Success -> {
                    _state.value = RestaurantDetailUiState.Loaded(result.data)
                    if (result.data.enriching) {
                        pollUntilEnriched()
                    }
                }

                is Result.Error -> _state.value = RestaurantDetailUiState.Error
            }
        }
    }

    private suspend fun pollUntilEnriched() {
        repeat(ENRICHMENT_POLL_MAX_ATTEMPTS) {
            delay(ENRICHMENT_POLL_INTERVAL_MS)
            when (val result = getRestaurantById(restaurantId)) {
                is Result.Success -> {
                    _state.value = RestaurantDetailUiState.Loaded(result.data)
                    if (!result.data.enriching) {
                        return
                    }
                }

                // A transient error mid-poll shouldn't strand the user on a silent
                // spinner — surface the same timeout/retry fallback as hitting the cap.
                is Result.Error -> {
                    markEnrichmentTimedOut()
                    return
                }
            }
        }

        markEnrichmentTimedOut()
    }

    private fun markEnrichmentTimedOut() {
        val current = _state.value
        if (current is RestaurantDetailUiState.Loaded && current.restaurant.enriching) {
            _state.value = current.copy(enrichmentTimedOut = true)
        }
    }

    override fun onCleared() {
        loadJob?.cancel()
        super.onCleared()
    }
}
