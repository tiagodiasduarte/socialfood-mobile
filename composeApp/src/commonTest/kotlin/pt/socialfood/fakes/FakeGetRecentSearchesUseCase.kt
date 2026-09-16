package pt.socialfood.fakes

import pt.socialfood.domain.model.RecentSearch
import pt.socialfood.domain.usecase.GetRecentSearchesUseCase

class FakeGetRecentSearchesUseCase(private val searches: List<RecentSearch> = emptyList()) : GetRecentSearchesUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(): List<RecentSearch> {
        invokeCount++
        return searches
    }
}
