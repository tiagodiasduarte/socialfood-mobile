package pt.socialfood.domain.usecase

import pt.socialfood.domain.model.RecentSearch
import pt.socialfood.domain.repository.SettingsRepository

class GetRecentSearchesUseCaseImpl(private val settingsRepository: SettingsRepository) : GetRecentSearchesUseCase {
    override suspend operator fun invoke(): List<RecentSearch> = settingsRepository.getRecentSearches()
}
