package pt.socialfood.fakes

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.repository.ConfigsRepository

class FakeConfigsRepository(private val result: Result<Configs>) : ConfigsRepository {
    var getConfigsInvokeCount: Int = 0
        private set

    override suspend fun getConfigs(): Result<Configs> {
        getConfigsInvokeCount++
        return result
    }
}
