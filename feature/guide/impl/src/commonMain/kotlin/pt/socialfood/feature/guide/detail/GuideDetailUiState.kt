package pt.socialfood.feature.guide.detail

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Guide

sealed interface GuideDetailUiState {
    data object Loading : GuideDetailUiState
    data class Loaded(
        val guide: Guide,
        val currentUserId: String?,
        val isFavourite: Boolean = false,
        val isLeaving: Boolean = false,
    ) : GuideDetailUiState
    data class Error(val errorCode: ErrorCode) : GuideDetailUiState
}
