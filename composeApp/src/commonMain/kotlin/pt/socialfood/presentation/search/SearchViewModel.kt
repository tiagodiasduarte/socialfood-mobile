package pt.socialfood.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.search.SearchUseCase
import pt.socialfood.presentation.error.toErrorCode
import kotlin.time.Duration.Companion.milliseconds

private const val PAGE_SIZE = 20
private const val MIN_QUERY_LENGTH = 2
private val SEARCH_DEBOUNCE_MS = 300.milliseconds

class SearchViewModel(private val search: SearchUseCase) : ViewModel() {

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Loaded(emptyList()))
    val state: StateFlow<SearchUiState> = _state

    var searchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.value = SearchUiState.Loaded(emptyList())
            return
        }
        if (query.length < MIN_QUERY_LENGTH) {
            return
        }
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            _state.value = SearchUiState.Loading
            when (val result = search(page = 1, limit = PAGE_SIZE, query = query)) {
                is Result.Success -> _state.value = SearchUiState.Loaded(result.data)
                is Result.Failure -> _state.value = SearchUiState.Error(result.error.toErrorCode())
            }
        }
    }
}
