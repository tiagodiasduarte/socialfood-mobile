package pt.socialfood.domain.usecase.search

import pt.socialfood.domain.model.Place

interface SaveRecentSearchedPlaceUseCase {
    suspend operator fun invoke(place: Place): List<Place>
}
