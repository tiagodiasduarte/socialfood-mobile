package pt.socialfood.presentation.guides

import pt.socialfood.domain.model.Guide

sealed interface GuidesUiState {
    data object Loading : GuidesUiState
    data class Loaded(
        val allGuides: List<Guide>,
        val myGuides: List<Guide>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : GuidesUiState
    data object Error : GuidesUiState
}
