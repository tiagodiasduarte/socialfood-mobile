package pt.socialfood.domain.repository

interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()

    suspend fun getPendingVerificationEmail(): String?
    suspend fun savePendingVerificationEmail(email: String)
    suspend fun clearPendingVerificationEmail()

    suspend fun getLastFavouritesSyncUpdate(): String?
    suspend fun saveLastFavouritesSyncUpdate(lastUpdate: String)

    suspend fun getLastFavouritesSyncAttemptAt(): Long?
    suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long)

    suspend fun getLastFavouriteRestaurantsSyncUpdate(): String?
    suspend fun saveLastFavouriteRestaurantsSyncUpdate(lastUpdate: String)

    suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long?
    suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long)
}
