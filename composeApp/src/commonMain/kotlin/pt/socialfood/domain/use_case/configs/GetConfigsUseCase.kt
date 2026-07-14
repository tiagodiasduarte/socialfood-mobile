package pt.socialfood.domain.use_case.configs

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Configs

interface GetConfigsUseCase {
    suspend operator fun invoke(): Result<Configs>
}