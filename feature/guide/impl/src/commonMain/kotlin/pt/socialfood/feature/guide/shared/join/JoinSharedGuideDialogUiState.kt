package pt.socialfood.feature.guide.shared.join

import pt.socialfood.core.common.error.ErrorCode

sealed interface JoinSharedGuideDialogUiState {
    data object Idle : JoinSharedGuideDialogUiState
    data object Loading : JoinSharedGuideDialogUiState
    data class Error(val errorCode: ErrorCode) : JoinSharedGuideDialogUiState
}
