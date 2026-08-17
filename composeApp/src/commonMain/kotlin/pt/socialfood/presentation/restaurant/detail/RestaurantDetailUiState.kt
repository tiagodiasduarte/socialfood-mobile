package pt.socialfood.presentation.restaurant.detail

import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus

sealed interface RestaurantDetailUiState {
    data object Loading : RestaurantDetailUiState
    data class Loaded(
        val restaurant: Restaurant,
        val isFavourite: Boolean = false,
        val visitStatus: VisitStatus? = null,
    ) : RestaurantDetailUiState
    data class Error(val errorCode: ErrorCode) : RestaurantDetailUiState
}
