package pt.socialfood.presentation.home

import pt.socialfood.domain.error.ErrorCode

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Loaded(
        val favouriteRestaurantIds: Set<String> = emptySet(),
        val favouriteGuideIds: Set<String> = emptySet(),
    ) : HomeUiState
    data class Error(val errorCode: ErrorCode) : HomeUiState
}
