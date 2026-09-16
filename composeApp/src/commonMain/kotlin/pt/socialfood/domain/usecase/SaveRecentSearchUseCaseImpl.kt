package pt.socialfood.domain.usecase

import pt.socialfood.domain.model.RecentSearch
import pt.socialfood.domain.repository.SettingsRepository

class SaveRecentSearchUseCaseImpl(private val settingsRepository: SettingsRepository) : SaveRecentSearchUseCase {
    override suspend operator fun invoke(search: RecentSearch): List<RecentSearch> {
        val current = settingsRepository.getRecentSearches()
        val updated = (listOf(search) + current.filterNot { it.id == search.id && it.type == search.type })
            .take(MAX_RECENT_SEARCHES)
        settingsRepository.saveRecentSearches(updated)
        return updated
    }

    private companion object {
        const val MAX_RECENT_SEARCHES = 10
    }
}
