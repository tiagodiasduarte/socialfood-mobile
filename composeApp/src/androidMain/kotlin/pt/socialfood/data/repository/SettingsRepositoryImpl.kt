package pt.socialfood.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import pt.socialfood.domain.repository.SettingsRepository

private val Context.dataStore by preferencesDataStore(name = "socialfood_settings")

private val KEY_TOKEN = stringPreferencesKey("jwt_token")
private val KEY_PENDING_VERIFICATION_EMAIL = stringPreferencesKey("pending_verification_email")
private val KEY_FAVOURITES_SYNC_CHECKPOINT = stringPreferencesKey("favourites_sync_checkpoint")
private val KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT = longPreferencesKey("last_favourites_sync_attempt_at")
private val KEY_FAVOURITE_RESTAURANTS_SYNC_CHECKPOINT = stringPreferencesKey("favourite_restaurants_sync_checkpoint")
private val KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT = longPreferencesKey("last_favourite_restaurants_sync_attempt_at")

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

    override suspend fun getFavouritesSyncCheckpoint(): String? =
        context.dataStore.data.first()[KEY_FAVOURITES_SYNC_CHECKPOINT]

    override suspend fun saveFavouritesSyncCheckpoint(checkpoint: String) {
        context.dataStore.edit { it[KEY_FAVOURITES_SYNC_CHECKPOINT] = checkpoint }
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT] = timestamp }
    }

    override suspend fun getFavouriteRestaurantsSyncCheckpoint(): String? =
        context.dataStore.data.first()[KEY_FAVOURITE_RESTAURANTS_SYNC_CHECKPOINT]

    override suspend fun saveFavouriteRestaurantsSyncCheckpoint(checkpoint: String) {
        context.dataStore.edit { it[KEY_FAVOURITE_RESTAURANTS_SYNC_CHECKPOINT] = checkpoint }
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? =
        context.dataStore.data.first()[KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT]

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        context.dataStore.edit { it[KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT] = timestamp }
    }
}
