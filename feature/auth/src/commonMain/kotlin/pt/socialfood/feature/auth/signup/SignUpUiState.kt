package pt.socialfood.feature.auth.signup

import org.jetbrains.compose.resources.StringResource
import pt.socialfood.core.common.error.ErrorCode

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val email: String) : SignUpUiState
    data class Error(val errorCode: ErrorCode) : SignUpUiState
    data class ValidationError(val message: StringResource) : SignUpUiState
}
