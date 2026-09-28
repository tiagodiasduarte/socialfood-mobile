package pt.socialfood.domain.usecase.search

import pt.socialfood.domain.model.Place
import pt.socialfood.domain.repository.SettingsRepository

class GetRecentSearchedPlacesUseCaseImpl(private val settingsRepository: SettingsRepository) :
    GetRecentSearchedPlacesUseCase {
    override suspend operator fun invoke(): List<Place> = settingsRepository.getRecentSearchedPlaces()
}
