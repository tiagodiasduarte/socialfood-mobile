package pt.socialfood.presentation.home

import pt.socialfood.domain.model.HomeSection

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Loaded(val sections: List<HomeSection>) : HomeUiState
    data object Error : HomeUiState
}
