package pt.socialfood.domain.repository

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Configs

interface ConfigsRepository {
    suspend fun getConfigs(): Result<Configs>
}
