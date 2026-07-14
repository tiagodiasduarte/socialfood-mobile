package pt.socialfood.presentation.validate_token

import pt.socialfood.domain.error.ErrorEntity

sealed interface ValidateTokenUiState {
    data object Idle : ValidateTokenUiState
    data object Loading : ValidateTokenUiState
    data object Success : ValidateTokenUiState
    data class Error(val error: ErrorEntity) : ValidateTokenUiState
}
