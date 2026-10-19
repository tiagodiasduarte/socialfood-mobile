package pt.socialfood.presentation.restaurant.search

import pt.socialfood.core.common.error.ErrorCode
import pt.socialfood.core.model.Place

sealed interface SearchRestaurantsUiState {
    data object Loading : SearchRestaurantsUiState
    data class Loaded(val places: List<Place>) : SearchRestaurantsUiState
    data class Error(val errorCode: ErrorCode) : SearchRestaurantsUiState
}
