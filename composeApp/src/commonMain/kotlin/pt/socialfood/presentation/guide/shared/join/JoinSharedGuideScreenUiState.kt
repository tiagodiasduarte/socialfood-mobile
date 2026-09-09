package pt.socialfood.presentation.guide.shared.join

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Guide

sealed interface JoinSharedGuideScreenUiState {
    data object Loading : JoinSharedGuideScreenUiState

    data class Loaded(val guide: Guide, val isJoining: Boolean = false, val joinErrorCode: ErrorCode? = null) :
        JoinSharedGuideScreenUiState

    data class Error(val errorCode: ErrorCode) : JoinSharedGuideScreenUiState
}
