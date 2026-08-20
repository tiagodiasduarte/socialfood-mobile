package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.usecase.home.GetHomeSectionsUseCase

class FakeGetHomeSectionsUseCase(private val result: Result<List<HomeSection>> = Result.Success(emptyList())) :
    GetHomeSectionsUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<List<HomeSection>> {
        invokeCount++
        return result
    }
}
