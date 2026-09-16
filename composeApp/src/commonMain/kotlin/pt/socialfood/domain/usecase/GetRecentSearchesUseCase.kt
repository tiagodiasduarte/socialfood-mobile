package pt.socialfood.domain.usecase

import pt.socialfood.domain.model.RecentSearch

interface GetRecentSearchesUseCase {
    suspend operator fun invoke(): List<RecentSearch>
}
