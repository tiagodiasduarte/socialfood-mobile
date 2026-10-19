package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.SettingsRepository

internal class RestartSignUpUseCaseImpl(private val settingsRepository: SettingsRepository) : RestartSignUpUseCase {
    override suspend operator fun invoke(): Result<Boolean> {
        settingsRepository.clearPendingVerificationEmail()
        return Result.Success(true)
    }
}
