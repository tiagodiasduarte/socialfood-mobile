package pt.socialfood.presentation.guide.shared.join

import pt.socialfood.domain.error.ErrorCode

sealed interface JoinSharedGuideDialogUiState {
    data object Idle : JoinSharedGuideDialogUiState

    data object Loading : JoinSharedGuideDialogUiState

    data class Error(val errorCode: ErrorCode) : JoinSharedGuideDialogUiState
}
