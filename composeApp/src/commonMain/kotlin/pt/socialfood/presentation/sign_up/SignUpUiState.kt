package pt.socialfood.presentation.sign_up

import org.jetbrains.compose.resources.StringResource
import pt.socialfood.domain.error.ErrorCode

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val email: String) : SignUpUiState
    data class Error(val errorCode: ErrorCode) : SignUpUiState
    data class ValidationError(val message: StringResource) : SignUpUiState
}
