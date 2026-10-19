package pt.socialfood.presentation.map.restaurant

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Restaurant

sealed interface MapRestaurantUiState {
    data object Loading : MapRestaurantUiState
    data class Loaded(val restaurant: Restaurant) : MapRestaurantUiState
    data class Error(val errorCode: ErrorCode) : MapRestaurantUiState
}
