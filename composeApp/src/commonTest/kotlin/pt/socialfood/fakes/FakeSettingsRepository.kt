package pt.socialfood.fakes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import pt.socialfood.domain.model.ThemeMode
import pt.socialfood.domain.repository.SettingsRepository

class FakeSettingsRepository : SettingsRepository {

    private var token: String? = null
    private val themeMode = MutableStateFlow(ThemeMode.LIGHT)
    private var pendingVerificationEmail: String? = null
    private var lastFavouritesSyncedAt: String? = null
    private var lastFavouritesSyncAttemptAt: Long? = null
    private var lastFavouriteRestaurantsSyncedAt: String? = null
    private var lastFavouriteRestaurantsSyncAttemptAt: Long? = null
    private var lastRestaurantVisitStatusSyncedAt: String? = null
    private var lastRestaurantVisitStatusSyncAttemptAt: Long? = null

    override suspend fun getToken(): String? = token

    override suspend fun saveToken(token: String) {
        this.token = token
    }

    override suspend fun clearToken() {
        token = null
    }

    override fun observeThemeMode(): Flow<ThemeMode> = themeMode

    override suspend fun saveThemeMode(mode: ThemeMode) {
        themeMode.value = mode
    }

    override suspend fun getPendingVerificationEmail(): String? = pendingVerificationEmail

    override suspend fun savePendingVerificationEmail(email: String) {
        pendingVerificationEmail = email
    }

    override suspend fun clearPendingVerificationEmail() {
        pendingVerificationEmail = null
    }

    override suspend fun getLastFavouritesSyncedAt(): String? = lastFavouritesSyncedAt

    override suspend fun saveLastFavouritesSyncedAt(syncedAt: String) {
        lastFavouritesSyncedAt = syncedAt
    }

    override suspend fun getLastFavouritesSyncAttemptAt(): Long? = lastFavouritesSyncAttemptAt

    override suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long) {
        lastFavouritesSyncAttemptAt = timestamp
    }

    override suspend fun getLastFavouriteRestaurantsSyncedAt(): String? = lastFavouriteRestaurantsSyncedAt

    override suspend fun saveLastFavouriteRestaurantsSyncedAt(syncedAt: String) {
        lastFavouriteRestaurantsSyncedAt = syncedAt
    }

    override suspend fun getLastFavouriteRestaurantsSyncAttemptAt(): Long? = lastFavouriteRestaurantsSyncAttemptAt

    override suspend fun saveLastFavouriteRestaurantsSyncAttemptAt(timestamp: Long) {
        lastFavouriteRestaurantsSyncAttemptAt = timestamp
    }

    override suspend fun getLastRestaurantVisitStatusSyncedAt(): String? = lastRestaurantVisitStatusSyncedAt

    override suspend fun saveLastRestaurantVisitStatusSyncedAt(syncedAt: String) {
        lastRestaurantVisitStatusSyncedAt = syncedAt
    }

    override suspend fun getLastRestaurantVisitStatusSyncAttemptAt(): Long? = lastRestaurantVisitStatusSyncAttemptAt

    override suspend fun saveLastRestaurantVisitStatusSyncAttemptAt(timestamp: Long) {
        lastRestaurantVisitStatusSyncAttemptAt = timestamp
    }
}
