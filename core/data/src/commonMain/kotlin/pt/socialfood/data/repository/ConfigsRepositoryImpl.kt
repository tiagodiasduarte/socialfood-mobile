package pt.socialfood.data.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.ConfigsRepository
import pt.socialfood.core.model.Configs
import pt.socialfood.data.api.ConfigsApi
import pt.socialfood.domain.error.safeApiCall

internal class ConfigsRepositoryImpl(private val configsApi: ConfigsApi) : ConfigsRepository {

    override suspend fun getConfigs(): Result<Configs> = safeApiCall { configsApi.getConfigs() }
}
