package pt.socialfood.fakes

import kotlinx.io.IOException
import pt.socialfood.data.api.ConfigsApi
import pt.socialfood.domain.model.Configs

class FakeConfigsApi(private val shouldThrow: Boolean = false) : ConfigsApi {

    override suspend fun getConfigs(): Configs {
        if (shouldThrow) throw IOException("test error")
        return Configs(version = "1.0.0")
    }
}
