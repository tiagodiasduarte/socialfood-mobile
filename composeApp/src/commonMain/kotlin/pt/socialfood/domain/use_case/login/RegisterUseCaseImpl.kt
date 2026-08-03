package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.domain.repository.SettingsRepository

class RegisterUseCaseImpl(
    private val repository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : RegisterUseCase {
    override suspend operator fun invoke(
        name: String,
        email: String,
        password: String
    ): Result<Boolean> {
        return when (val result = repository.register(name, email, password)) {
            is Result.Success -> {
                settingsRepository.savePendingVerificationEmail(email)
                Result.Success(true)
            }
            is Result.Failure -> Result.Failure(result.error)
        }
    }
}
