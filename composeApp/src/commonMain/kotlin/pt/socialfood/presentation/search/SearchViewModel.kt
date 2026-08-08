package pt.socialfood.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.search.SearchUseCase
import pt.socialfood.presentation.error.toErrorCode
import kotlin.time.Duration.Companion.milliseconds

private const val PAGE_SIZE = 50
private const val MIN_QUERY_LENGTH = 3
private val SEARCH_DEBOUNCE_MS = 300.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(private val search: SearchUseCase) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val state: StateFlow<SearchUiState> = _query
        .debounce(SEARCH_DEBOUNCE_MS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            when {
                query.isBlank() -> flowOf(SearchUiState.Loaded(emptyList()))
                query.length < MIN_QUERY_LENGTH -> emptyFlow()
                else -> performSearch(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SearchUiState.Loaded(emptyList()))

    fun onSearchQueryChange(query: String) {
        _query.value = query
    }

    private fun performSearch(query: String): Flow<SearchUiState> = flow {
        emit(SearchUiState.Loading)
        when (val result = search(page = 1, limit = PAGE_SIZE, query = query)) {
            is Result.Success -> emit(SearchUiState.Loaded(result.data))
            is Result.Failure -> emit(SearchUiState.Error(result.error.toErrorCode()))
        }
    }
}
