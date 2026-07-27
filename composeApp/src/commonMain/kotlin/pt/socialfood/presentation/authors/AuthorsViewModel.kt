package pt.socialfood.presentation.authors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.use_case.author.FindAuthorsUseCase
import pt.socialfood.domain.use_case.author.GetAuthorsPagingUseCase

private const val PAGE_SIZE = 20
private const val SEARCH_DEBOUNCE_MS = 300L

class AuthorsViewModel(
    private val findAuthors: FindAuthorsUseCase,
    private val getAuthorsPaging: GetAuthorsPagingUseCase,
) : ViewModel() {

    val authors: Flow<PagingData<Author>> = getAuthorsPaging().cachedIn(viewModelScope)

    private val _state = MutableStateFlow<AuthorsUiState>(AuthorsUiState.Loading)
    val state: StateFlow<AuthorsUiState> = _state

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private var currentQuery: String? = null

    init {
        // The blank-query browse list is served by `authors` (Paging + cache) instead, so this
        // collector only ever needs to (re)load a search result page — it's a no-op once the
        // query is cleared back to blank.
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _searchQuery
                .debounce(SEARCH_DEBOUNCE_MS)
                .collectLatest { query ->
                    if (query.isBlank()) return@collectLatest
                    currentQuery = query
                    loadFirstPage()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun loadFirstPage() {
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
