package pt.socialfood.presentation.sign_in

import pt.socialfood.domain.error.ErrorCode

sealed interface SignInUiState {
    data object Idle : SignInUiState
    data object Loading : SignInUiState
    data object Success : SignInUiState
    data class Error(val errorCode: ErrorCode) : SignInUiState
}
