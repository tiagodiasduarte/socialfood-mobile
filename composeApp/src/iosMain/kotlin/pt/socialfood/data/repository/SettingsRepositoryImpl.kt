package pt.socialfood.data.repository

import platform.Foundation.NSUserDefaults
import pt.socialfood.domain.repository.SettingsRepository

private const val KEY_TOKEN = "jwt_token"
private const val KEY_PENDING_VERIFICATION_EMAIL = "pending_verification_email"
private const val KEY_LAST_FAVOURITES_SYNCED_AT = "favourites_synced_at"
private const val KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT = "last_favourites_sync_attempt_at"
private const val KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT = "favourite_restaurants_synced_at"
private const val KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT = "last_favourite_restaurants_sync_attempt_at"

@Suppress("TooManyFunctions")
class SettingsRepositoryImpl : SettingsRepository {
    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun getToken(): String? = defaults.stringForKey(KEY_TOKEN)

    override suspend fun saveToken(token: String) {
        defaults.setObject(token, KEY_TOKEN)
    }

    override suspend fun clearToken() {
        defaults.removeObjectForKey(KEY_TOKEN)
    }

    override suspend fun getPendingVerificationEmail(): String? = defaults.stringForKey(KEY_PENDING_VERIFICATION_EMAIL)

    override suspend fun savePendingVerificationEmail(email: String) {
        defaults.setObject(email, KEY_PENDING_VERIFICATION_EMAIL)
    }

    override suspend fun clearPendingVerificationEmail() {
        defaults.removeObjectForKey(KEY_PENDING_VERIFICATION_EMAIL)
    }

    override suspend fun getLastFavouritesSyncedAt(): String? = defaults.stringForKey(KEY_LAST_FAVOURITES_SYNCED_AT)

    override suspend fun saveLastFavouritesSyncedAt(syncedAt: String) {
        defaults.setObject(syncedAt, KEY_LAST_FAVOURITES_SYNCED_AT)
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? =
        if (defaults.objectForKey(KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT) != null) {
            defaults.integerForKey(KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT)
        } else {
            null
        }

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        defaults.setInteger(timestamp, KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT)
    }

    override suspend fun getLastFavouriteRestaurantsSyncedAt(): String? =
        defaults.stringForKey(KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT)

    override suspend fun saveLastFavouriteRestaurantsSyncedAt(syncedAt: String) {
        defaults.setObject(syncedAt, KEY_LAST_FAVOURITE_RESTAURANTS_SYNCED_AT)
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? =
        if (defaults.objectForKey(KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT) != null) {
            defaults.integerForKey(KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT)
        } else {
            null
        }

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        defaults.setInteger(timestamp, KEY_LAST_FAVOURITE_RESTAURANTS_SYNC_ATTEMPT_AT)
    }
}
