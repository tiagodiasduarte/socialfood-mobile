package pt.socialfood.fakes

import pt.socialfood.domain.model.Place
import pt.socialfood.domain.usecase.SaveRecentSearchedPlaceUseCase

class FakeSaveRecentSearchedPlaceUseCase(private val result: List<Place> = emptyList()) :
    SaveRecentSearchedPlaceUseCase {
    var invokeCount: Int = 0
        private set
    var lastPlace: Place? = null
        private set

    override suspend operator fun invoke(place: Place): List<Place> {
        invokeCount++
        lastPlace = place
        return result
    }
}
