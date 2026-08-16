package pt.socialfood.presentation.restaurant.visited

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant

sealed interface VisitedRestaurantsUiState {
    data object Loading : VisitedRestaurantsUiState
    data class Loaded(val restaurants: List<Restaurant>, val hasMore: Boolean, val isLoadingMore: Boolean = false) :
        VisitedRestaurantsUiState
    data class Error(val errorCode: ErrorCode) : VisitedRestaurantsUiState
}
