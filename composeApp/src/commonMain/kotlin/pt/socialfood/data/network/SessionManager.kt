package pt.socialfood.data.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.socialfood.domain.repository.SettingsRepository

class SessionManager(private val settingsRepository: SettingsRepository) {
    private val _unauthorizedEvent = MutableSharedFlow<Unit>(replay = 0)
    val unauthorizedEvent: SharedFlow<Unit> = _unauthorizedEvent

    // Lets KtorHttpClient know it must drop its cached bearer token, since a new one was
    // just saved or the session was cleared.
    private val _tokensChangedEvent = MutableSharedFlow<Unit>(replay = 0)
    val tokensChangedEvent: SharedFlow<Unit> = _tokensChangedEvent

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var accessToken: String? = null
        private set

    var refreshToken: String? = null
        private set

    init {
        accessToken = runBlocking { settingsRepository.getToken() }
        refreshToken = runBlocking { settingsRepository.getRefreshToken() }
    }

    suspend fun saveTokens(newAccessToken: String, newRefreshToken: String) {
        accessToken = newAccessToken
        refreshToken = newRefreshToken
        settingsRepository.saveToken(newAccessToken)
        settingsRepository.saveRefreshToken(newRefreshToken)

        scope.launch {
            _tokensChangedEvent.emit(Unit)
        }
    }

    suspend fun clear() {
        accessToken = null
        refreshToken = null
        settingsRepository.clearToken()
        settingsRepository.clearRefreshToken()

        scope.launch {
            _tokensChangedEvent.emit(Unit)
            _unauthorizedEvent.emit(Unit)
        }
    }
}
