package pt.socialfood.domain.usecase.search

import pt.socialfood.domain.model.Place

interface GetRecentSearchedPlacesUseCase {
    suspend operator fun invoke(): List<Place>
}
