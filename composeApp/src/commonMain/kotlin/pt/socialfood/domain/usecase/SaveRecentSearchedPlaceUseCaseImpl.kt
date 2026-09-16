package pt.socialfood.domain.usecase

import pt.socialfood.domain.model.Place
import pt.socialfood.domain.repository.SettingsRepository

class SaveRecentSearchedPlaceUseCaseImpl(private val settingsRepository: SettingsRepository) :
    SaveRecentSearchedPlaceUseCase {
    override suspend operator fun invoke(place: Place): List<Place> {
        val current = settingsRepository.getRecentSearchedPlaces()
        val updated = (listOf(place) + current.filterNot { it.id == place.id }).take(MAX_RECENT_SEARCHED_PLACES)
        settingsRepository.saveRecentSearchedPlaces(updated)
        return updated
    }

    private companion object {
        const val MAX_RECENT_SEARCHED_PLACES = 10
    }
}
