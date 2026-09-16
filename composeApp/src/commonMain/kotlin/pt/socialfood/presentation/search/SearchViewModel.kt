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
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.RecentSearch
import pt.socialfood.domain.model.RecentSearchType
import pt.socialfood.domain.model.Search
import pt.socialfood.domain.usecase.GetRecentSearchesUseCase
import pt.socialfood.domain.usecase.SaveRecentSearchUseCase
import pt.socialfood.domain.usecase.search.GetGuideSuggestionsUseCase
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
    private val getGuideSuggestions: GetGuideSuggestionsUseCase,
    private val getRecentSearches: GetRecentSearchesUseCase,
    private val saveRecentSearch: SaveRecentSearchUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _suggestionResultsRequested = MutableStateFlow(false)
    val suggestionResultsRequested: StateFlow<Boolean> = _suggestionResultsRequested.asStateFlow()

    private val _activeSuggestionSource = MutableStateFlow<SuggestionSource?>(null)
    val activeSuggestionSource: StateFlow<SuggestionSource?> = _activeSuggestionSource.asStateFlow()

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Loaded(emptyList()))
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<RecentSearch>>(emptyList())
    val recentSearches: StateFlow<List<RecentSearch>> = _recentSearches.asStateFlow()

    private var suggestionsJob: Job? = null
    private var lastSuggestionsAction: (() -> Unit)? = null

    init {
        _query
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.length >= MIN_QUERY_LENGTH) performSearch(query) else emptyFlow()
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            _recentSearches.value = getRecentSearches()
        }
    }

    fun onSearchQueryChange(query: String) {
        _query.value = query
        _suggestionResultsRequested.value = false
        _activeSuggestionSource.value = null
    }

    fun onFavoriteRestaurantsClick() {
        lastSuggestionsAction = ::onFavoriteRestaurantsClick
        requestSuggestions(SuggestionSource.RESTAURANTS) {
            performSuggestions(fetch = { getRestaurantSuggestions() }) { suggestions ->
                suggestions.restaurants.map { Search.RestaurantResult(it) }
            }
        }
    }

    fun onFavoriteGuidesClick() {
        lastSuggestionsAction = ::onFavoriteGuidesClick
        requestSuggestions(SuggestionSource.GUIDES) {
            performSuggestions(fetch = { getGuideSuggestions() }) { suggestions ->
                suggestions.guides.map { Search.GuideResult(it) }
            }
        }
    }

    fun retrySuggestions() {
        lastSuggestionsAction?.invoke()
    }

    fun onClearSuggestions() {
        suggestionsJob?.cancel()
        suggestionsJob = null
        lastSuggestionsAction = null
        _activeSuggestionSource.value = null
        _suggestionResultsRequested.value = false
        _state.value = SearchUiState.Loaded(emptyList())
    }

    fun onResultClick(result: Search) {
        viewModelScope.launch {
            _recentSearches.value = saveRecentSearch(result.toRecentSearch())
        }
    }

    fun onRecentSearchClick(recentSearch: RecentSearch) {
        viewModelScope.launch {
            _recentSearches.value = saveRecentSearch(recentSearch)
        }
    }

    private fun requestSuggestions(source: SuggestionSource, perform: () -> Flow<SearchUiState>) {
        _activeSuggestionSource.value = source
        _suggestionResultsRequested.value = true
        suggestionsJob?.cancel()
        suggestionsJob = perform().onEach { _state.value = it }.launchIn(viewModelScope)
    }

    private fun performSearch(query: String): Flow<SearchUiState> = flow {
        emit(SearchUiState.Loading)
        when (val result = search(page = 1, limit = PAGE_SIZE, query = query)) {
            is Result.Success -> emit(SearchUiState.Loaded(result.data))
            is Result.Failure -> emit(SearchUiState.Error(result.error.toErrorCode()))
        }
    }

    private fun <T> performSuggestions(
        fetch: suspend () -> Result<T>,
        toResults: (T) -> List<Search>,
    ): Flow<SearchUiState> = flow {
        emit(SearchUiState.Loading)
        when (val result = fetch()) {
            is Result.Success -> emit(SearchUiState.Loaded(toResults(result.data)))
            is Result.Failure -> emit(SearchUiState.Error(result.error.toErrorCode()))
        }
    }
}

private fun Search.toRecentSearch(): RecentSearch = when (this) {
    is Search.RestaurantResult ->
        RecentSearch(id = restaurant.id, type = RecentSearchType.RESTAURANT, title = restaurant.name)
    is Search.GuideResult -> RecentSearch(id = guide.id, type = RecentSearchType.GUIDE, title = guide.name)
    is Search.AuthorResult -> RecentSearch(id = author.id, type = RecentSearchType.AUTHOR, title = author.name)
}
