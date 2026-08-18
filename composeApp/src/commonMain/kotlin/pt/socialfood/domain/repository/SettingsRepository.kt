package pt.socialfood.domain.repository

import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.model.VisitStatus

@Suppress("TooManyFunctions")
interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()

    suspend fun getThemeMode(): ThemeMode
    suspend fun saveThemeMode(mode: ThemeMode)

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

    suspend fun getLastRestaurantVisitStatusSyncedAt(status: VisitStatus): String?
    suspend fun saveLastRestaurantVisitStatusSyncedAt(status: VisitStatus, syncedAt: String)

    suspend fun getLastRestaurantVisitStatusSyncAttemptAt(status: VisitStatus): Long?
    suspend fun saveLastRestaurantVisitStatusSyncAttemptAt(status: VisitStatus, timestamp: Long)
}
