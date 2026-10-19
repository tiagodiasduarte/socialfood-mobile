package pt.socialfood.core.domain.usecase.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pt.socialfood.core.common.Result
import pt.socialfood.core.domain.repository.AuthRepository
import pt.socialfood.core.domain.repository.LocalCacheRepository
import pt.socialfood.core.domain.repository.UsersRepository
import pt.socialfood.core.domain.session.SessionManager

internal class LogoutUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
    private val localCacheRepository: LocalCacheRepository,
    private val userRepository: UsersRepository,
) : LogoutUseCase {
    override suspend operator fun invoke(): Result<Boolean> {
        // Clear local state first so the UI can navigate away immediately, without
        // waiting on the (possibly slow/retried) server call below.
        localCacheRepository.clearAll()
        sessionManager.clear()
        userRepository.clearUser()

        // Best-effort server-side session invalidation, fired off in the background.
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            repository.logout()
        }

        return Result.Success(true)
    }
}
