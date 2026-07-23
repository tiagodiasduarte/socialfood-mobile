package pt.socialfood.presentation.favourite.guide

import pt.socialfood.domain.model.Guide

sealed interface FavouriteGuidesUiState {
    data object Loading : FavouriteGuidesUiState
    data class Loaded(
        val guides: List<Guide>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : FavouriteGuidesUiState
    data object Error : FavouriteGuidesUiState
}
