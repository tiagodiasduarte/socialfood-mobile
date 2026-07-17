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

    var pendingVerificationEmail: String? = null
        private set

    init {
        init()
    }

    fun init() {
        token = settings.getStringOrNull(KEY_TOKEN)
        pendingVerificationEmail = settings.getStringOrNull(KEY_PENDING_VERIFICATION_EMAIL)
    }

    fun saveToken(newToken: String) {
        token = newToken
        settings.putString(KEY_TOKEN, newToken)
    }

    fun savePendingVerification(email: String) {
        pendingVerificationEmail = email
        settings.putString(KEY_PENDING_VERIFICATION_EMAIL, email)
    }

    fun clearPendingVerification() {
        pendingVerificationEmail = null
        settings.remove(KEY_PENDING_VERIFICATION_EMAIL)
    }

    fun clear() {
        token = null
        settings.remove(KEY_TOKEN)

        pendingVerificationEmail = null
        settings.remove(KEY_PENDING_VERIFICATION_EMAIL)

        scope.launch {
            _unauthorizedEvent.emit(Unit)
        }
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_PENDING_VERIFICATION_EMAIL = "pending_verification_email"
    }
}