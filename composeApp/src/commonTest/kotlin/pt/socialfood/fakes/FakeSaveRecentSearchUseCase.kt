package pt.socialfood.fakes

import pt.socialfood.domain.model.RecentSearch
import pt.socialfood.domain.usecase.SaveRecentSearchUseCase

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
