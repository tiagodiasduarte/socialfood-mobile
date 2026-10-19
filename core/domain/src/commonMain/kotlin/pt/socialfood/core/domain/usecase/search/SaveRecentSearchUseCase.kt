package pt.socialfood.core.domain.usecase.search

import pt.socialfood.core.model.RecentSearch

interface SaveRecentSearchUseCase {
    suspend operator fun invoke(search: RecentSearch): List<RecentSearch>
}
