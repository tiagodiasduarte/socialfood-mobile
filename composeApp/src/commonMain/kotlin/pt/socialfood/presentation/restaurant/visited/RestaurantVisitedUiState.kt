package pt.socialfood.presentation.restaurant.visited

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant

sealed interface RestaurantVisitedUiState {
    data object Loading : RestaurantVisitedUiState
    data class Loaded(val restaurants: List<Restaurant>, val hasMore: Boolean, val isLoadingMore: Boolean = false) :
        RestaurantVisitedUiState
    data class Error(val errorCode: ErrorCode) : RestaurantVisitedUiState
}
