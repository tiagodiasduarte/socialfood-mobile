package pt.socialfood.presentation.guide.shared

import pt.socialfood.domain.error.ErrorCode

sealed interface JoinGuideUiState {
    data object Idle : JoinGuideUiState

    data object Loading : JoinGuideUiState

    data class Error(val errorCode: ErrorCode) : JoinGuideUiState
}
