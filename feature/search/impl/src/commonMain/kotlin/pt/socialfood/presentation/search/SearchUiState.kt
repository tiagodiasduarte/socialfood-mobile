package pt.socialfood.presentation.search

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Search

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data class Loaded(val results: List<Search>) : SearchUiState
    data class Error(val errorCode: ErrorCode) : SearchUiState
}
