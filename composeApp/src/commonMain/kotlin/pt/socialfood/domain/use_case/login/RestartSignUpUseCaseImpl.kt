package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.SettingsRepository

class RestartSignUpUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : RestartSignUpUseCase {
    override suspend operator fun invoke(): Result<Boolean> {
        settingsRepository.clearPendingVerificationEmail()
        return Result.Success(true)
    }
}
