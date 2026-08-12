package pt.socialfood.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.usecase.search.GetRestaurantSuggestionsUseCase
import pt.socialfood.domain.usecase.search.SearchUseCase
import pt.socialfood.presentation.error.toErrorCode
import kotlin.time.Duration.Companion.milliseconds

private const val PAGE_SIZE = 50
internal const val MIN_QUERY_LENGTH = 3
private val SEARCH_DEBOUNCE_MS = 300.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val search: SearchUseCase,
    private val getRestaurantSuggestions: GetRestaurantSuggestionsUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestionResultsRequested = MutableStateFlow(false)
    val suggestionResultsRequested: StateFlow<Boolean> = _suggestionResultsRequested.asStateFlow()

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Loaded(emptyList()))
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var restaurantSuggestionsJob: Job? = null

    init {
        _query
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.length >= MIN_QUERY_LENGTH) performSearch(query) else emptyFlow()
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _query.value = query
        _suggestionResultsRequested.value = false
    }

    fun onFavoriteRestaurantsClick() {
        _suggestionResultsRequested.value = true
        restaurantSuggestionsJob?.cancel()
        restaurantSuggestionsJob = performRestaurantSuggestions()
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    private fun performSearch(query: String): Flow<SearchUiState> = flow {
        emit(SearchUiState.Loading)
        when (val result = search(page = 1, limit = PAGE_SIZE, query = query)) {
            is Result.Success -> emit(SearchUiState.Loaded(result.data))
            is Result.Failure -> emit(SearchUiState.Error(result.error.toErrorCode()))
        }
    }

    private fun performRestaurantSuggestions(): Flow<SearchUiState> = flow {
        emit(SearchUiState.Loading)
        when (val result = getRestaurantSuggestions()) {
            is Result.Success -> emit(
                SearchUiState.Loaded(result.data.restaurants.map { Search.RestaurantResult(it) }),
            )
            is Result.Failure -> emit(SearchUiState.Error(result.error.toErrorCode()))
        }
    }
}
