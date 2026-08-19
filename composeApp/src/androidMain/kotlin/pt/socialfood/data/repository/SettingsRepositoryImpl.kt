package pt.socialfood.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import pt.socialfood.data.security.TokenCipher
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.SettingsRepository

private val Context.dataStore by preferencesDataStore(name = "socialfood_settings")

private val USER_JWT_TOKEN = stringPreferencesKey("user_jwt_token")
private val THEME_MODE = stringPreferencesKey("theme_mode")
private val PENDING_VERIFICATION_EMAIL = stringPreferencesKey("pending_verification_email")
private val LAST_FAVOURITES_SYNCED_AT = stringPreferencesKey("last_favourites_synced_at")
private val LAST_FAVOURITES_SYNC_ATTEMPT_AT = longPreferencesKey("last_favourites_sync_attempt_at")
private val LAST_FAVOURITE_RESTAURANTS_SYNCED_AT = stringPreferencesKey("last_favourite_restaurants_synced_at")
private val LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT =
    longPreferencesKey("last_favourite_restaurants_sync_attempt_at")

private val LAST_RESTAURANT_VISIT_STATUS_SYNCED_AT = stringPreferencesKey("last_restaurant_visit_status_synced_at")
private val LAST_RESTAURANT_VISIT_STATUS_SYNC_ATTEMPT_AT =
    longPreferencesKey("last_restaurant_visit_status_sync_attempt_at")

@Suppress("TooManyFunctions")
class SettingsRepositoryImpl(private val context: Context) : SettingsRepository {
    private val tokenCipher = TokenCipher()

    override suspend fun getToken(): String? {
        val stored = context.dataStore.data.first()[USER_JWT_TOKEN] ?: return null
        return tokenCipher.decryptOrNull(stored)
    }

    override suspend fun saveToken(token: String) {
        context.dataStore.edit { it[USER_JWT_TOKEN] = tokenCipher.encrypt(token) }
    }

    override suspend fun clearToken() {
        context.dataStore.edit { it.remove(USER_JWT_TOKEN) }
    }

    override fun observeThemeMode(): Flow<ThemeMode> =
        context.dataStore.data.map { prefs -> prefs[THEME_MODE]?.let { ThemeMode.valueOf(it) } ?: ThemeMode.LIGHT }

    override suspend fun saveThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[THEME_MODE] = mode.name }
    }

    override suspend fun getPendingVerificationEmail(): String? =
        context.dataStore.data.first()[PENDING_VERIFICATION_EMAIL]

    override suspend fun savePendingVerificationEmail(email: String) {
        context.dataStore.edit { it[PENDING_VERIFICATION_EMAIL] = email }
    }

    override suspend fun clearPendingVerificationEmail() {
        context.dataStore.edit { it.remove(PENDING_VERIFICATION_EMAIL) }
    }

    override suspend fun getLastFavouritesSyncedAt(): String? =
        context.dataStore.data.first()[LAST_FAVOURITES_SYNCED_AT]

    override suspend fun saveLastFavouritesSyncedAt(syncedAt: String) {
        context.dataStore.edit { it[LAST_FAVOURITES_SYNCED_AT] = syncedAt }
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? =
        context.dataStore.data.first()[LAST_FAVOURITES_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[LAST_FAVOURITES_SYNC_ATTEMPT_AT] = timestamp }
    }

    override suspend fun getLastFavouriteRestaurantsSyncedAt(): String? =
        context.dataStore.data.first()[LAST_FAVOURITE_RESTAURANTS_SYNCED_AT]

    override suspend fun saveLastFavouriteRestaurantsSyncedAt(syncedAt: String) {
        context.dataStore.edit { it[LAST_FAVOURITE_RESTAURANTS_SYNCED_AT] = syncedAt }
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? =
        context.dataStore.data.first()[LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT] = timestamp }
    }

    override suspend fun getLastRestaurantVisitStatusSyncedAt(status: VisitStatus): String? =
        context.dataStore.data.first()[LAST_RESTAURANT_VISIT_STATUS_SYNCED_AT]

    override suspend fun saveLastRestaurantVisitStatusSyncedAt(status: VisitStatus, syncedAt: String) {
        context.dataStore.edit { it[LAST_RESTAURANT_VISIT_STATUS_SYNCED_AT] = syncedAt }
    }

    override suspend fun getLastRestaurantVisitStatusSyncAttemptAt(status: VisitStatus): Long? =
        context.dataStore.data.first()[LAST_RESTAURANT_VISIT_STATUS_SYNC_ATTEMPT_AT]

    override suspend fun saveLastRestaurantVisitStatusSyncAttemptAt(status: VisitStatus, timestamp: Long) {
        context.dataStore.edit { it[LAST_RESTAURANT_VISIT_STATUS_SYNC_ATTEMPT_AT] = timestamp }
    }
}
