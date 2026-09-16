package pt.socialfood.fakes

import pt.socialfood.domain.model.Place
import pt.socialfood.domain.usecase.GetRecentSearchedPlacesUseCase

class FakeGetRecentSearchedPlacesUseCase(private val places: List<Place> = emptyList()) :
    GetRecentSearchedPlacesUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(): List<Place> {
        invokeCount++
        return places
    }
}
