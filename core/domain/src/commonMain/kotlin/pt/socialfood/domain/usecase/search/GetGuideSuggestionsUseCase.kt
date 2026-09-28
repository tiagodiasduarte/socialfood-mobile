package pt.socialfood.domain.usecase.search

import pt.socialfood.core.Result
import pt.socialfood.domain.model.GuideSuggestions

interface GetGuideSuggestionsUseCase {
    suspend operator fun invoke(): Result<GuideSuggestions>
}
