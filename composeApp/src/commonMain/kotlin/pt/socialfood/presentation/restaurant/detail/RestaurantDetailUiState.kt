package pt.socialfood.presentation.restaurant.detail

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant

sealed interface RestaurantDetailUiState {
    data object Loading : RestaurantDetailUiState
    data class Loaded(
        val restaurant: Restaurant,
        val isFavourite: Boolean = false,
    ) : RestaurantDetailUiState
    data class Error(val errorCode: ErrorCode) : RestaurantDetailUiState
}
