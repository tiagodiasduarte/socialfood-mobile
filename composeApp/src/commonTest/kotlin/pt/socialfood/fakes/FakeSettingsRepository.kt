package pt.socialfood.fakes

import pt.socialfood.domain.repository.SettingsRepository

class FakeSettingsRepository : SettingsRepository {

    private var token: String? = null
    private var pendingVerificationEmail: String? = null
    private var favouritesSyncCheckpoint: String? = null
    private var lastFavouritesSyncAttemptAt: Long? = null
    private var favouriteRestaurantsSyncCheckpoint: String? = null
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

    override suspend fun getFavouritesSyncCheckpoint(): String? = favouritesSyncCheckpoint

    override suspend fun saveFavouritesSyncCheckpoint(checkpoint: String) {
        favouritesSyncCheckpoint = checkpoint
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? = lastFavouritesSyncAttemptAt

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        lastFavouritesSyncAttemptAt = timestamp
    }

    override suspend fun getFavouriteRestaurantsSyncCheckpoint(): String? = favouriteRestaurantsSyncCheckpoint

    override suspend fun saveFavouriteRestaurantsSyncCheckpoint(checkpoint: String) {
        favouriteRestaurantsSyncCheckpoint = checkpoint
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? = lastFavouriteRestaurantsSyncAttemptAt

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        lastFavouriteRestaurantsSyncAttemptAt = timestamp
    }
}
