package pt.socialfood.presentation.profile

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.AuthorDetail

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(val author: AuthorDetail) : ProfileUiState
    data class Error(val errorCode: ErrorCode) : ProfileUiState
}
