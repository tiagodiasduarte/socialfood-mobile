package pt.socialfood.presentation.guide.list

import pt.socialfood.domain.model.Guide

sealed interface GuidesUiState {
    data object Loading : GuidesUiState
    data class Loaded(
        val guides: List<Guide>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : GuidesUiState
    data object Error : GuidesUiState
}
