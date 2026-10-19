package pt.socialfood.core.testing.fakes

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.ConfigsRepository
import pt.socialfood.core.model.Configs

class FakeConfigsRepository(private val result: Result<Configs>) : ConfigsRepository {
    var getConfigsInvokeCount: Int = 0
        private set

    override suspend fun getConfigs(): Result<Configs> {
        getConfigsInvokeCount++
        return result
    }
}
