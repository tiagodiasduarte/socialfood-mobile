package pt.socialfood.feature.home

import pt.socialfood.core.common.error.ErrorCode

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Loaded(
        val favouriteRestaurantIds: Set<String> = emptySet(),
        val favouriteGuideIds: Set<String> = emptySet(),
    ) : HomeUiState
    data class Error(val errorCode: ErrorCode) : HomeUiState
}
