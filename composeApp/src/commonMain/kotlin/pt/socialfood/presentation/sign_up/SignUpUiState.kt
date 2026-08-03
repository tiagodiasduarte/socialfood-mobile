package pt.socialfood.presentation.sign_up

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val email: String) : SignUpUiState
    data class Error(val message: String) : SignUpUiState
}
