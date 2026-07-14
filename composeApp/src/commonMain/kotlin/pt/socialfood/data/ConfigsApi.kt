package pt.socialfood.data

import pt.socialfood.domain.model.Configs

interface ConfigsApi {
    suspend fun getConfigs(): Configs
}