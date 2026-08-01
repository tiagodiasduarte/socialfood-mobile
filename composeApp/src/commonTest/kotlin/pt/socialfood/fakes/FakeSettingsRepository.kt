package pt.socialfood.fakes

import pt.socialfood.domain.repository.SettingsRepository

class FakeSettingsRepository : SettingsRepository {

    private var token: String? = null
    private var pendingVerificationEmail: String? = null
    private var lastFavouritesSyncUpdate: String? = null
    private var lastFavouritesSyncAttemptAt: Long? = null
    private var lastFavouriteRestaurantsSyncUpdate: String? = null
    private var lastFavouriteRestaurantsSyncAttemptAt: Long? = null

    override suspend fun getToken(): String? = token

    override suspend fun saveToken(token: String) {
        this.token = token
    }

    override suspend fun clearToken() {
        token = null
    }

    override suspend fun getPendingVerificationEmail(): String? = pendingVerificationEmail

    override suspend fun savePendingVerificationEmail(email: String) {
        pendingVerificationEmail = email
    }

    override suspend fun clearPendingVerificationEmail() {
        pendingVerificationEmail = null
    }

    override suspend fun getLastFavouritesSyncUpdate(): String? = lastFavouritesSyncUpdate

    override suspend fun saveLastFavouritesSyncUpdate(lastUpdate: String) {
        lastFavouritesSyncUpdate = lastUpdate
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? = lastFavouritesSyncAttemptAt

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        lastFavouritesSyncAttemptAt = timestamp
    }

    override suspend fun getLastFavouriteRestaurantsSyncUpdate(): String? = lastFavouriteRestaurantsSyncUpdate

    override suspend fun saveLastFavouriteRestaurantsSyncUpdate(lastUpdate: String) {
        lastFavouriteRestaurantsSyncUpdate = lastUpdate
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? = lastFavouriteRestaurantsSyncAttemptAt

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        lastFavouriteRestaurantsSyncAttemptAt = timestamp
    }
}
