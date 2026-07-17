package pt.socialfood.data.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.socialfood.domain.repository.SettingsRepository

class SessionManager(
    private val settingsRepository: SettingsRepository
) {
    private val _unauthorizedEvent = MutableSharedFlow<Unit>(replay = 0)
    val unauthorizedEvent: SharedFlow<Unit> = _unauthorizedEvent

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var token: String? = null
        private set

    init {
        token = runBlocking { settingsRepository.getToken() }
    }

    suspend fun saveToken(newToken: String) {
        token = newToken
        settingsRepository.saveToken(newToken)
    }

    suspend fun clear() {
        token = null
        settingsRepository.clearToken()

        scope.launch {
            _unauthorizedEvent.emit(Unit)
        }
    }
}