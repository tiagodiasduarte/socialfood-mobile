package pt.socialfood.fakes

import pt.socialfood.domain.repository.SettingsRepository

class FakeSettingsRepository : SettingsRepository {

    private var token: String? = null

    override suspend fun getToken(): String? = token

    override suspend fun saveToken(token: String) {
        this.token = token
    }

    override suspend fun clearToken() {
        token = null
    }
}
