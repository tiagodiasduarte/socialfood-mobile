package pt.socialfood.presentation.sign_up

import pt.socialfood.domain.error.ErrorEntity

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val email: String) : SignUpUiState
    data class Error(val error: ErrorEntity) : SignUpUiState
}
