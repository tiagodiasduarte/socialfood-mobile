package pt.socialfood.presentation.search

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Search

sealed interface SearchUiState {
    data object Loading : SearchUiState
    data class Loaded(val results: List<Search>) : SearchUiState
    data class Error(val errorCode: ErrorCode) : SearchUiState
}
