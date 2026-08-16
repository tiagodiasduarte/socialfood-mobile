package pt.socialfood.domain.repository

import pt.socialfood.domain.model.VisitStatus

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

    suspend fun getLastRestaurantVisitSyncedAt(status: VisitStatus): String?
    suspend fun saveLastRestaurantVisitSyncedAt(status: VisitStatus, syncedAt: String)

    suspend fun getLastRestaurantVisitSyncAttemptAt(status: VisitStatus): Long?
    suspend fun saveLastRestaurantVisitSyncAttemptAt(status: VisitStatus, timestamp: Long)
}
