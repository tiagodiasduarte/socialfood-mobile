package pt.socialfood.domain.usecase

import pt.socialfood.domain.model.RecentSearch

interface SaveRecentSearchUseCase {
    suspend operator fun invoke(search: RecentSearch): List<RecentSearch>
}
