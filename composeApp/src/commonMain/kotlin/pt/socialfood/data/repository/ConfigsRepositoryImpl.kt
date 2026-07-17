package pt.socialfood.data.repository

import pt.socialfood.core.Result
import pt.socialfood.data.ConfigsApi
import pt.socialfood.data.crash.CrashReporter
import pt.socialfood.data.network.extensions.toErrorEntity
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.repository.ConfigsRepository

class ConfigsRepositoryImpl(
    private val configsApi: ConfigsApi,
    private val crashReporter: CrashReporter,
) : ConfigsRepository {

    override suspend fun getConfigs(): Result<Configs> {
        return try {
            val configs = configsApi.getConfigs()
            Result.Success(configs)
        } catch (exception: Exception) {
            crashReporter.recordException(exception)
            Result.Error(exception.toErrorEntity())
        }
    }
}