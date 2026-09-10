package pt.socialfood.presentation.guide.shared.join

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Guide

data class JoinSharedGuideCardUiState(
    val guide: Guide,
    val isJoining: Boolean = false,
    val joinErrorCode: ErrorCode? = null,
)
