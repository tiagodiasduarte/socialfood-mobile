package pt.socialfood.domain.use_case.login

import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.domain.repository.SettingsRepository

class ValidateCodeUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : ValidateCodeUseCase {
    override suspend operator fun invoke(email: String, code: String): Result<Boolean> {
        val result = repository.validateCode(email = email, code = code)

        if (result is Result.Success) {
            sessionManager.saveToken(result.data)
            settingsRepository.clearPendingVerificationEmail()
            return Result.Success(true)
        }

        return Result.Error(ErrorEntity.Unknown)
    }
}
