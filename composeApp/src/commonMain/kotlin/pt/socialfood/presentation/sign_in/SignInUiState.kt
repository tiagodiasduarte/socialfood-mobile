package pt.socialfood.presentation.sign_in

sealed interface SignInUiState {
    data object Idle : SignInUiState
    data object Loading : SignInUiState
    data object Success : SignInUiState
    data class Error(val message: String) : SignInUiState
}
