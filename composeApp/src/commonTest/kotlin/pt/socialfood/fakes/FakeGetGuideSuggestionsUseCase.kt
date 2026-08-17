package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.GuideSuggestions
import pt.socialfood.domain.usecase.search.GetGuideSuggestionsUseCase

class FakeGetGuideSuggestionsUseCase(
    private val result: Result<GuideSuggestions> = Result.Success(
        GuideSuggestions(guides = emptyList(), generatedAt = ""),
    ),
) : GetGuideSuggestionsUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<GuideSuggestions> {
        invokeCount++
        return result
    }
}
