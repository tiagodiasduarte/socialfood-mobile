package pt.socialfood.domain.usecase.configs

import pt.socialfood.core.Result
import pt.socialfood.domain.model.Configs
import pt.socialfood.domain.repository.ConfigsRepository

class GetConfigsUseCaseImpl(
    private val repository: ConfigsRepository
) : GetConfigsUseCase {
    override suspend operator fun invoke(): Result<Configs> = repository.getConfigs()
}
