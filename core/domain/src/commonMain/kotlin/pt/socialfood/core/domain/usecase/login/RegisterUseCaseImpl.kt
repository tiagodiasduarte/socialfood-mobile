package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.AuthRepository
import pt.socialfood.core.domain.repository.SettingsRepository

internal class RegisterUseCaseImpl(
    private val repository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : RegisterUseCase {
    override suspend operator fun invoke(name: String, email: String, password: String): Result<Boolean> =
        when (val result = repository.register(name, email, password)) {
            is Result.Success -> {
                settingsRepository.savePendingVerificationEmail(email)
                Result.Success(true)
            }
            is Result.Failure -> Result.Failure(result.error)
        }
}
