package pt.socialfood.presentation.signin

import org.jetbrains.compose.resources.StringResource
import pt.socialfood.domain.error.ErrorCode

sealed interface SignInUiState {
    data object Idle : SignInUiState
    data object Loading : SignInUiState
    data object Success : SignInUiState
    data class Error(val errorCode: ErrorCode) : SignInUiState
    data class ValidationError(val message: StringResource) : SignInUiState
}
