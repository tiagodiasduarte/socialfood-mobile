package pt.socialfood.presentation.guide.shared.join

import pt.socialfood.domain.error.ErrorCode

sealed interface JoinSharedGuideUiState {
    data object Idle : JoinSharedGuideUiState

    data object Loading : JoinSharedGuideUiState

    data class Error(val errorCode: ErrorCode) : JoinSharedGuideUiState
}
