package pt.socialfood.domain.repository

@Suppress("TooManyFunctions")
interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()

    suspend fun getPendingVerificationEmail(): String?
    suspend fun savePendingVerificationEmail(email: String)
    suspend fun clearPendingVerificationEmail()

    suspend fun getLastFavouritesSyncedAt(): String?
    suspend fun saveLastFavouritesSyncedAt(syncedAt: String)

    suspend fun getLastFavouritesSyncAttemptAt(): Long?
    suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long)

    suspend fun getLastFavouriteRestaurantsSyncedAt(): String?
    suspend fun saveLastFavouriteRestaurantsSyncedAt(syncedAt: String)

    suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long?
    suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long)

    suspend fun getLastWishlistRestaurantsSyncedAt(): String?
    suspend fun saveLastWishlistRestaurantsSyncedAt(syncedAt: String)

    suspend fun getLastWishlistRestaurantsSyncAttemptAt(): Long?
    suspend fun saveLastWishlistRestaurantsSyncAttemptAt(timestamp: Long)
}
