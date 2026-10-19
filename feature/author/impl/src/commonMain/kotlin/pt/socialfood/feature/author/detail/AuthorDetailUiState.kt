package pt.socialfood.feature.author.detail

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.AuthorDetail

sealed interface AuthorDetailUiState {
    data object Loading : AuthorDetailUiState
    data class Loaded(val author: AuthorDetail) : AuthorDetailUiState
    data class Error(val errorCode: ErrorCode) : AuthorDetailUiState
}
