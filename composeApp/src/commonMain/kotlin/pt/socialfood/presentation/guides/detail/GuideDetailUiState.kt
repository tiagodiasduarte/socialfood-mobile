package pt.socialfood.presentation.guides.detail

import pt.socialfood.domain.model.Guide

sealed interface GuideDetailUiState {
    data object Loading : GuideDetailUiState
    data class Loaded(val guide: Guide, val currentUserId: String?) : GuideDetailUiState
    data object Error : GuideDetailUiState
}
