package pt.socialfood.data.api

import pt.socialfood.core.model.Configs

interface ConfigsApi {
    suspend fun getConfigs(): Configs
}
