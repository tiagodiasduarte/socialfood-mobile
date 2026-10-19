package pt.socialfood.core.domain.usecase.search

import pt.socialfood.core.model.Place

interface SaveRecentSearchedPlaceUseCase {
    suspend operator fun invoke(place: Place): List<Place>
}
