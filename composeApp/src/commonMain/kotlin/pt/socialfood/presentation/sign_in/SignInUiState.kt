package pt.socialfood.presentation.sign_in

import pt.socialfood.domain.error.ErrorEntity

sealed interface SignInUiState {
    data object Idle : SignInUiState
    data object Loading : SignInUiState
    data object Success : SignInUiState
    data class Error(val error: ErrorEntity, val message: String? = null) : SignInUiState
}
