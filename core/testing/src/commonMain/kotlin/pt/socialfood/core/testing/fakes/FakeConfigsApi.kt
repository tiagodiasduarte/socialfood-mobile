package pt.socialfood.core.testing.fakes

import kotlinx.io.IOException
import pt.socialfood.core.model.Configs
import pt.socialfood.core.network.api.ConfigsApi

class FakeConfigsApi(private val shouldThrow: Boolean = false) : ConfigsApi {

    override suspend fun getConfigs(): Configs {
        if (shouldThrow) throw IOException("test error")
        return Configs(version = "1.0.0")
    }
}
