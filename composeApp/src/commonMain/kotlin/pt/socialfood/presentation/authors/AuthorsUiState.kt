package pt.socialfood.presentation.authors

import pt.socialfood.domain.model.Author

sealed interface AuthorsUiState {
    data object Loading : AuthorsUiState
    data class Loaded(
        val authors: List<Author>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : AuthorsUiState
    data object Error : AuthorsUiState
}
