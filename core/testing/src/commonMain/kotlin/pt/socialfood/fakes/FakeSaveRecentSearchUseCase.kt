package pt.socialfood.fakes

import pt.socialfood.core.domain.usecase.search.SaveRecentSearchUseCase
import pt.socialfood.core.model.RecentSearch

class FakeSaveRecentSearchUseCase(private val result: List<RecentSearch> = emptyList()) : SaveRecentSearchUseCase {
    var invokeCount: Int = 0
        private set
    var lastSearch: RecentSearch? = null
        private set

    override suspend operator fun invoke(search: RecentSearch): List<RecentSearch> {
        invokeCount++
        lastSearch = search
        return result
    }
}
