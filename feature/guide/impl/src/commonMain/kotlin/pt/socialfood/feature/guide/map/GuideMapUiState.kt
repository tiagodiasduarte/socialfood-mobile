package pt.socialfood.feature.guide.map

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Guide

sealed interface GuideMapUiState {
    data object Loading : GuideMapUiState
    data class Loaded(val guide: Guide) : GuideMapUiState
    data class Error(val errorCode: ErrorCode) : GuideMapUiState
}
