package pt.socialfood.data.repository

import platform.Foundation.NSUserDefaults
import pt.socialfood.domain.repository.SettingsRepository

private const val KEY_TOKEN = "jwt_token"

class SettingsRepositoryImpl : SettingsRepository {

    private val defaults = NSUserDefaults.standardUserDefaults

    override suspend fun getToken(): String? = defaults.stringForKey(KEY_TOKEN)

    override suspend fun saveToken(token: String) {
        defaults.setObject(token, KEY_TOKEN)
    }

    override suspend fun clearToken() {
        defaults.removeObjectForKey(KEY_TOKEN)
    }
}
