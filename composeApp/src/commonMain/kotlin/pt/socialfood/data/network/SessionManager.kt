package pt.socialfood.data.network


import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SessionManager(
    private val settings: Settings
) {
    private val _unauthorizedEvent = MutableSharedFlow<Unit>(replay = 0)
    val unauthorizedEvent: SharedFlow<Unit> = _unauthorizedEvent

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var token: String? = null
        private set

    init {
        init()
    }

    fun init() {
        token = settings.getStringOrNull(KEY_TOKEN)
    }

    fun saveToken(newToken: String) {
        token = newToken
        settings.putString(KEY_TOKEN, newToken)
    }

    fun clear() {
        token = null
        settings.remove(KEY_TOKEN)

        scope.launch {
            _unauthorizedEvent.emit(Unit)
        }
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
    }
}