package pt.socialfood.domain.usecase.search

import pt.socialfood.domain.model.RecentSearch

interface SaveRecentSearchUseCase {
    suspend operator fun invoke(search: RecentSearch): List<RecentSearch>
}
