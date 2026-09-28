package pt.socialfood.data.api

import pt.socialfood.domain.model.Configs

interface ConfigsApi {
    suspend fun getConfigs(): Configs
}
