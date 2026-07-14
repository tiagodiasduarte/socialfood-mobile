package pt.socialfood.presentation.authors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.use_case.author.FindAuthorsUseCase

private const val PAGE_SIZE = 20
private const val SEARCH_DEBOUNCE_MS = 300L

class AuthorsViewModel(
    private val findAuthors: FindAuthorsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<AuthorsUiState>(AuthorsUiState.Loading)
    val state: StateFlow<AuthorsUiState> = _state

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private var currentQuery: String? = null

    init {
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _searchQuery
                .debounce(SEARCH_DEBOUNCE_MS)
                .collectLatest { query ->
                    currentQuery = query.ifBlank { null }
                    loadFirstPage()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadFirstPage() {
        viewModelScope.launch {
            _state.value = AuthorsUiState.Loading
            currentPage = 1
            when (val result = findAuthors(page = 1, limit = PAGE_SIZE, query = currentQuery)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = AuthorsUiState.Loaded(
                        authors = result.data.authors,
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = AuthorsUiState.Error
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            currentPage = 1
            when (val result = findAuthors(page = 1, limit = PAGE_SIZE, query = currentQuery)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = AuthorsUiState.Loaded(
                        authors = result.data.authors,
                        hasMore = result.data.hasMore,
                    )
                }
                is Result.Error -> _state.value = AuthorsUiState.Error
            }
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val current = _state.value as? AuthorsUiState.Loaded ?: return
        if (!current.hasMore || current.isLoadingMore) return

        viewModelScope.launch {
            _state.value = current.copy(isLoadingMore = true)
            val nextPage = currentPage + 1
            when (val result = findAuthors(page = nextPage, limit = PAGE_SIZE, query = currentQuery)) {
                is Result.Success -> {
                    currentPage = result.data.page
                    _state.value = current.copy(
                        authors = current.authors + result.data.authors,
                        hasMore = result.data.hasMore,
                        isLoadingMore = false,
                    )
                }
                is Result.Error -> {
                    _state.value = current.copy(isLoadingMore = false)
                }
            }
        }
    }
}
