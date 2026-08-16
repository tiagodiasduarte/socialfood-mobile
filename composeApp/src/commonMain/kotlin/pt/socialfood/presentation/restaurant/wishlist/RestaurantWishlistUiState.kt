package pt.socialfood.presentation.restaurant.wishlist

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant

sealed interface RestaurantWishlistUiState {
    data object Loading : RestaurantWishlistUiState
    data class Loaded(val restaurants: List<Restaurant>, val hasMore: Boolean, val isLoadingMore: Boolean = false) :
        RestaurantWishlistUiState
    data class Error(val errorCode: ErrorCode) : RestaurantWishlistUiState
}
