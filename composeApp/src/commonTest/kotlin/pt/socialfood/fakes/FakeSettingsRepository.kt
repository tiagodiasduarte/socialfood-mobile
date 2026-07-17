package pt.socialfood.fakes

import pt.socialfood.domain.repository.SettingsRepository

class FakeSettingsRepository : SettingsRepository {

    private var token: String? = null
    private var pendingVerificationEmail: String? = null

    override suspend fun getToken(): String? = token

    override suspend fun saveToken(token: String) {
        this.token = token
    }

    override suspend fun clearToken() {
        token = null
    }

    override suspend fun getPendingVerificationEmail(): String? = pendingVerificationEmail

    override suspend fun savePendingVerificationEmail(email: String) {
        pendingVerificationEmail = email
    }

    override suspend fun clearPendingVerificationEmail() {
        pendingVerificationEmail = null
    }
}
