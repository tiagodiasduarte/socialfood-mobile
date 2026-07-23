package pt.socialfood.presentation.favourites

import pt.socialfood.domain.model.Guide

sealed interface FavouritesUiState {
    data object Loading : FavouritesUiState
    data class Loaded(
        val guides: List<Guide>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : FavouritesUiState
    data object Error : FavouritesUiState
}
