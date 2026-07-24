package pt.socialfood.presentation.favourite.restaurant

import pt.socialfood.domain.model.Restaurant

sealed interface FavouriteRestaurantsUiState {
    data object Loading : FavouriteRestaurantsUiState
    data class Loaded(
        val restaurants: List<Restaurant>,
        val hasMore: Boolean,
        val isLoadingMore: Boolean = false,
    ) : FavouriteRestaurantsUiState
    data object Error : FavouriteRestaurantsUiState
}
