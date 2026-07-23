package pt.socialfood.presentation.favourites

import pt.socialfood.domain.model.Guide

sealed interface FavouritesGuidesUiState {
    data object Loading : FavouritesGuidesUiState
    data class Loaded(
        val guides: List<Guide>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : FavouritesGuidesUiState
    data object Error : FavouritesGuidesUiState
}
