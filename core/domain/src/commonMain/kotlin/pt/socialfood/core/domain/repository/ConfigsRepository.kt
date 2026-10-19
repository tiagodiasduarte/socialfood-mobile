package pt.socialfood.core.domain.repository

import pt.socialfood.core.common.Result
import pt.socialfood.core.model.Configs

interface ConfigsRepository {
    suspend fun getConfigs(): Result<Configs>
}
