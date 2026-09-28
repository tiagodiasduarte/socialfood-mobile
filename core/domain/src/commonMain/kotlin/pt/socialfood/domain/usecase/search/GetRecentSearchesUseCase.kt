package pt.socialfood.domain.usecase.search

import pt.socialfood.domain.model.RecentSearch

interface GetRecentSearchesUseCase {
    suspend operator fun invoke(): List<RecentSearch>
}
