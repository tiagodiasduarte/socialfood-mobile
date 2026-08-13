package pt.socialfood.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import pt.socialfood.data.security.TokenCipher
import pt.socialfood.domain.repository.SettingsRepository

private val Context.dataStore by preferencesDataStore(name = "socialfood_settings")

private val KEY_TOKEN = stringPreferencesKey("jwt_token")
private val KEY_PENDING_VERIFICATION_EMAIL = stringPreferencesKey("pending_verification_email")
private val KEY_LAST_FAVOURITES_SYNCED_AT = stringPreferencesKey("favourites_synced_at")
private val KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT = longPreferencesKey("last_favourites_sync_attempt_at")
private val KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT = stringPreferencesKey("favourite_restaurants_synced_at")
private val KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT =
    longPreferencesKey("last_favourite_restaurants_sync_attempt_at")

@Suppress("TooManyFunctions")
class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val tokenCipher = TokenCipher()

    @Suppress("ReturnCount")
    override suspend fun getToken(): String? {
        val stored = context.dataStore.data.first()[KEY_TOKEN] ?: return null
        tokenCipher.decryptOrNull(stored)?.let { return it }

        // Legacy plaintext token from before encryption was added: use it as-is and upgrade it
        // in place so future reads hit the encrypted path.
        saveToken(stored)
        return stored
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { it[KEY_TOKEN] = tokenCipher.encrypt(token) }
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

    override suspend fun getLastFavouritesSyncedAt(): String? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITES_SYNCED_AT]

    override suspend fun saveLastFavouritesSyncedAt(syncedAt: String) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITES_SYNCED_AT] = syncedAt }
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT] = timestamp }
    }

    override suspend fun getLastFavouriteRestaurantsSyncedAt(): String? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT]

    override suspend fun saveLastFavouriteRestaurantsSyncedAt(syncedAt: String) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT] = syncedAt }
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT] = timestamp }
    }
}
