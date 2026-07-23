package pt.socialfood.domain.repository

interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()

    suspend fun getPendingVerificationEmail(): String?
    suspend fun savePendingVerificationEmail(email: String)
    suspend fun clearPendingVerificationEmail()

    suspend fun getFavouritesSyncCheckpoint(): String?
    suspend fun saveFavouritesSyncCheckpoint(checkpoint: String)

    suspend fun getLastFavouritesSyncAttemptAt(): Long?
    suspend fun saveLastFavouritesSyncAttemptAt(timestamp: Long)
}
