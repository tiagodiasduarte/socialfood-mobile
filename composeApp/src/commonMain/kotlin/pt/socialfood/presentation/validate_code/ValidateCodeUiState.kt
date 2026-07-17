package pt.socialfood.presentation.validate_code

import pt.socialfood.domain.error.ErrorEntity

sealed interface ValidateCodeUiState {
    data object Idle : ValidateCodeUiState
    data object Loading : ValidateCodeUiState
    data object Success : ValidateCodeUiState
    data object RestartSignUp : ValidateCodeUiState
    data class Error(val error: ErrorEntity) : ValidateCodeUiState
}
