package pt.socialfood.fakes

import pt.socialfood.core.domain.usecase.search.SaveRecentSearchedPlaceUseCase
import pt.socialfood.core.model.Place

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
