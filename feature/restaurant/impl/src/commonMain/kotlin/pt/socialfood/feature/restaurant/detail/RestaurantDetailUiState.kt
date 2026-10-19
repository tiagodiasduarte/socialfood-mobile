package pt.socialfood.feature.restaurant.detail

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Restaurant
import pt.socialfood.core.model.VisitStatus

sealed interface RestaurantDetailUiState {
    data object Loading : RestaurantDetailUiState
    data class Loaded(
        val restaurant: Restaurant,
        val isFavourite: Boolean = false,
        val visitStatus: VisitStatus? = null,
    ) : RestaurantDetailUiState
    data class Error(val errorCode: ErrorCode) : RestaurantDetailUiState
}
