package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.use_case.configs.GetConfigsUseCase

class FakeGetConfigsUseCase(
    private val result: Result<Configs>,
) : GetConfigsUseCase {
    var invokeCount: Int = 0
        private set

    override suspend fun invoke(): Result<Configs> {
        invokeCount++
        return result
    }
}
