package pt.socialfood.feature.guide.shared.join

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Guide

data class JoinSharedGuideCardUiState(
    val guide: Guide,
    val isJoining: Boolean = false,
    val joinErrorCode: ErrorCode? = null,
)
