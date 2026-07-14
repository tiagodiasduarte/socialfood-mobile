package pt.socialfood.fakes

import pt.socialfood.data.ConfigsApi
import pt.socialfood.domain.model.Configs

class FakeConfigsApi(private val shouldThrow: Boolean = false) : ConfigsApi {

    override suspend fun getConfigs(): Configs {
        if (shouldThrow) throw RuntimeException("test error")
        return Configs(version = "1.0.0")
    }
}
