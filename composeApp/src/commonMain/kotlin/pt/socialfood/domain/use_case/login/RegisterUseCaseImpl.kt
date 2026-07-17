package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
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
        val result = repository.register(name, email, password)

        if (result is Result.Success) {
            settingsRepository.savePendingVerificationEmail(email)
            return Result.Success(true)
        }

        return Result.Error(ErrorEntity.Unknown)
    }
}
