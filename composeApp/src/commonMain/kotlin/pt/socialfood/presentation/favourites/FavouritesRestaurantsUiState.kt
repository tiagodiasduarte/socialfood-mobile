package pt.socialfood.presentation.favourites

import pt.socialfood.domain.model.Restaurant

sealed interface FavouritesRestaurantsUiState {
    data object Loading : FavouritesRestaurantsUiState
    data class Loaded(
        val restaurants: List<Restaurant>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : FavouritesRestaurantsUiState
    data object Error : FavouritesRestaurantsUiState
}
