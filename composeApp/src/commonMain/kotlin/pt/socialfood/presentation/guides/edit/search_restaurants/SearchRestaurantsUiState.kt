package pt.socialfood.presentation.guides.edit.search_restaurants

import pt.socialfood.domain.model.Place


sealed interface SearchRestaurantsUiState {
    data object Loading : SearchRestaurantsUiState
    data class Loaded(val places: List<Place>) : SearchRestaurantsUiState
    data object Error : SearchRestaurantsUiState
}
