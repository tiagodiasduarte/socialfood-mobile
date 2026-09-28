package pt.socialfood.domain.usecase.configs

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Configs

interface GetConfigsUseCase {
    suspend operator fun invoke(): Result<Configs>
}
