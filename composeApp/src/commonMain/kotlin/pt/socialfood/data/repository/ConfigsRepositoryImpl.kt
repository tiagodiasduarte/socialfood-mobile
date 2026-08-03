package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.api.ConfigsApi
import pt.socialfood.domain.error.safeApiCall
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.repository.ConfigsRepository

class ConfigsRepositoryImpl(
    private val configsApi: ConfigsApi
) : ConfigsRepository {

    override suspend fun getConfigs(): Result<Configs> {
        return safeApiCall { configsApi.getConfigs() }
    }
}
