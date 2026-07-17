package pt.socialfood.domain.repository

interface SettingsRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
    suspend fun clearToken()
}
