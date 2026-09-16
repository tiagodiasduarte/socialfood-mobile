package pt.socialfood.domain.usecase

import pt.socialfood.domain.model.Place

interface GetRecentSearchedPlacesUseCase {
    suspend operator fun invoke(): List<Place>
}
