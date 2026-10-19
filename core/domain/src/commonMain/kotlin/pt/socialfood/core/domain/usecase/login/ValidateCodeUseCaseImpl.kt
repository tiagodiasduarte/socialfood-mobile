package pt.socialfood.core.domain.usecase.login

import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.AuthRepository
import pt.socialfood.core.domain.repository.SettingsRepository
import pt.socialfood.core.domain.session.SessionManager

internal class ValidateCodeUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
    private val settingsRepository: SettingsRepository,
) : ValidateCodeUseCase {
    override suspend operator fun invoke(email: String, code: String): Result<Boolean> =
        when (val result = repository.validateCode(email = email, code = code)) {
            is Result.Success -> {
                sessionManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                settingsRepository.clearPendingVerificationEmail()
                Result.Success(true)
            }
            is Result.Failure -> Result.Failure(result.error)
        }
}
