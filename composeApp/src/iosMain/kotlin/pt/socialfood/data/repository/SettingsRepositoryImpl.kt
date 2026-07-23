package pt.socialfood.data.repository

import platform.Foundation.NSUserDefaults
import pt.socialfood.domain.repository.SettingsRepository

private const val KEY_TOKEN = "jwt_token"
private const val KEY_PENDING_VERIFICATION_EMAIL = "pending_verification_email"
private const val KEY_FAVOURITES_SYNC_CHECKPOINT = "favourites_sync_checkpoint"
private const val KEY_LAST_FAVOURITES_SYNC_ATTEMPT_AT = "last_favourites_sync_attempt_at"

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

    override suspend fun getFavouritesSyncCheckpoint(): String? = defaults.stringForKey(KEY_FAVOURITES_SYNC_CHECKPOINT)

    override suspend fun saveFavouritesSyncCheckpoint(checkpoint: String) {
        defaults.setObject(checkpoint, KEY_FAVOURITES_SYNC_CHECKPOINT)
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
}
