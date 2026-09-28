package pt.socialfood.presentation.map.restaurant

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant

sealed interface MapRestaurantUiState {
    data object Loading : MapRestaurantUiState
    data class Loaded(val restaurant: Restaurant) : MapRestaurantUiState
    data class Error(val errorCode: ErrorCode) : MapRestaurantUiState
}
