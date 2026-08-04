package pt.socialfood.presentation.guide.detail

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Guide

sealed interface GuideDetailUiState {
    data object Loading : GuideDetailUiState
    data class Loaded(
        val guide: Guide,
        val currentUserId: String?,
        val isFavourite: Boolean = false,
    ) : GuideDetailUiState
    data class Error(val errorCode: ErrorCode) : GuideDetailUiState
}
