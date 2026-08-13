package pt.socialfood.domain.usecase.search

import pt.socialfood.core.Result
import pt.socialfood.domain.model.GuideSuggestions
import pt.socialfood.domain.repository.SearchRepository

class GetGuideSuggestionsUseCaseImpl(private val repository: SearchRepository) : GetGuideSuggestionsUseCase {
    override suspend fun invoke(): Result<GuideSuggestions> = repository.getGuideSuggestions()
}
