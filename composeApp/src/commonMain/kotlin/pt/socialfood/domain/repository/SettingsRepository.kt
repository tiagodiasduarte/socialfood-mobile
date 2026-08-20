package pt.socialfood.domain.repository

import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.ThemeMode

@Suppress("TooManyFunctions")
interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()

    suspend fun getRefreshToken(): String?
    suspend fun saveRefreshToken(token: String)
    suspend fun clearRefreshToken()

    fun observeThemeMode(): Flow<ThemeMode>
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

    suspend fun getLastRestaurantVisitStatusSyncedAt(): String?
    suspend fun saveLastRestaurantVisitStatusSyncedAt(syncedAt: String)

    suspend fun getLastRestaurantVisitStatusSyncAttemptAt(): Long?
    suspend fun saveLastRestaurantVisitStatusSyncAttemptAt(timestamp: Long)
}
