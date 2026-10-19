package pt.socialfood.feature.auth.validatecode

import org.jetbrains.compose.resources.StringResource
import pt.socialfood.core.common.error.ErrorCode

sealed interface ValidateCodeUiState {
    data object Idle : ValidateCodeUiState
    data object Loading : ValidateCodeUiState
    data object Success : ValidateCodeUiState
    data object RestartSignUp : ValidateCodeUiState
    data class Error(val errorCode: ErrorCode) : ValidateCodeUiState
    data class ValidationError(val message: StringResource) : ValidateCodeUiState
}
