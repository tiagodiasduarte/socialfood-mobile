package pt.socialfood.domain.usecase.login

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.data.network.SessionManager
import pt.socialfood.domain.repository.AuthRepository
import pt.socialfood.domain.repository.LocalCacheRepository

class LogoutUseCaseImpl(
    private val sessionManager: SessionManager,
    private val repository: AuthRepository,
    private val localCacheRepository: LocalCacheRepository,
) : LogoutUseCase {
    override suspend operator fun invoke(): Result<Boolean> {
        // Clear local state first so the UI can navigate away immediately, without
        // waiting on the (possibly slow/retried) server call below.
        localCacheRepository.clearAll()
        sessionManager.clear()

        // Best-effort server-side session invalidation, fired off in the background.
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            repository.logout()
        }

        return Result.Success(true)
    }
}
