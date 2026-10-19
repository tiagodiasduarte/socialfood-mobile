package pt.socialfood.core.network.api

import pt.socialfood.core.model.Configs

interface ConfigsApi {
    suspend fun getConfigs(): Configs
}
