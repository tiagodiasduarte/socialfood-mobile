package pt.socialfood.presentation.guide.map

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Guide

sealed interface GuideMapUiState {
    data object Loading : GuideMapUiState
    data class Loaded(val guide: Guide) : GuideMapUiState
    data class Error(val errorCode: ErrorCode) : GuideMapUiState
}
