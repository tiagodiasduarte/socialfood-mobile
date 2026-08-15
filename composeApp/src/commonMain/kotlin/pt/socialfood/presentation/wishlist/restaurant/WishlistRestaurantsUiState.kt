package pt.socialfood.presentation.wishlist.restaurant

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant

sealed interface WishlistRestaurantsUiState {
    data object Loading : WishlistRestaurantsUiState
    data class Loaded(val restaurants: List<Restaurant>, val hasMore: Boolean, val isLoadingMore: Boolean = false) :
        WishlistRestaurantsUiState
    data class Error(val errorCode: ErrorCode) : WishlistRestaurantsUiState
}
