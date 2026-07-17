package pt.socialfood.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import pt.socialfood.domain.repository.SettingsRepository

private val Context.dataStore by preferencesDataStore(name = "socialfood_settings")

private val KEY_TOKEN = stringPreferencesKey("jwt_token")
private val KEY_PENDING_VERIFICATION_EMAIL = stringPreferencesKey("pending_verification_email")

class SettingsRepositoryImpl(
    private val context: Context,
) : SettingsRepository {

    override suspend fun getToken(): String? =
        context.dataStore.data.first()[KEY_TOKEN]

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = token }
    }

    override suspend fun clearToken() {
        context.dataStore.edit { it.remove(KEY_TOKEN) }
    }

    override suspend fun getPendingVerificationEmail(): String? =
        context.dataStore.data.first()[KEY_PENDING_VERIFICATION_EMAIL]

    override suspend fun savePendingVerificationEmail(email: String) {
        context.dataStore.edit { it[KEY_PENDING_VERIFICATION_EMAIL] = email }
    }

    override suspend fun clearPendingVerificationEmail() {
        context.dataStore.edit { it.remove(KEY_PENDING_VERIFICATION_EMAIL) }
    }
}
