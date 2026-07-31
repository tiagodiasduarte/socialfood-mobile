package pt.socialfood.data.repository

import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import pt.socialfood.core.Result
import pt.socialfood.data.api.ConfigsApi
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.repository.ConfigsRepository

class ConfigsRepositoryImpl(
    private val configsApi: ConfigsApi
) : ConfigsRepository {

    override suspend fun getConfigs(): Result<Configs> {
        return try {
            val configs = configsApi.getConfigs()
            Result.Success(configs)
        } catch (e: IOException) {
            Result.Error(e.toErrorEntity())
        } catch (e: ResponseException) {
            Result.Error(e.toErrorEntity())
        }
    }
}
