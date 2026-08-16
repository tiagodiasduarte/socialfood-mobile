package pt.socialfood.presentation.restaurantvisit

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant

sealed interface RestaurantVisitUiState {
    data object Loading : RestaurantVisitUiState
    data class Loaded(val restaurants: List<Restaurant>, val hasMore: Boolean, val isLoadingMore: Boolean = false) :
        RestaurantVisitUiState
    data class Error(val errorCode: ErrorCode) : RestaurantVisitUiState
}
