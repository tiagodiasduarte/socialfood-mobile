package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Place
import pt.socialfood.domain.use_case.SearchPlacesUseCase

class FakeSearchPlacesUseCase(
    private val result: Result<List<Place>> = Result.Success(emptyList()),
) : SearchPlacesUseCase {
    var invokeCount: Int = 0
        private set

    override suspend operator fun invoke(query: String): Result<List<Place>> {
        invokeCount++
        return result
    }
}
