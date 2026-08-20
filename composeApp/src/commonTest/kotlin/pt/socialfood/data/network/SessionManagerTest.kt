package pt.socialfood.data.network

import kotlinx.coroutines.test.runTest
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionManagerTest {

    @Test
    fun `given no tokens saved when session manager is created then tokens are null`() {
        // Given
        val settingsRepository = FakeSettingsRepository()

        // When
        val sessionManager = SessionManager(settingsRepository)

        // Then
        assertNull(sessionManager.accessToken)
        assertNull(sessionManager.refreshToken)
    }

    @Test
    fun `given tokens when saveTokens is called then tokens are set and persisted`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val sessionManager = SessionManager(settingsRepository)

        // When
        sessionManager.saveTokens("jwt-token", "refresh-token")

        // Then
        assertEquals("jwt-token", sessionManager.accessToken)
        assertEquals("refresh-token", sessionManager.refreshToken)
        assertEquals("jwt-token", settingsRepository.getToken())
        assertEquals("refresh-token", settingsRepository.getRefreshToken())
    }

    @Test
    fun `given persisted tokens when a new SessionManager is created then tokens are reloaded`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        SessionManager(settingsRepository).saveTokens("jwt-token", "refresh-token")

        // When
        val reloaded = SessionManager(settingsRepository)

        // Then
        assertEquals("jwt-token", reloaded.accessToken)
        assertEquals("refresh-token", reloaded.refreshToken)
    }

    @Test
    fun `given saved tokens when clear is called then tokens are cleared and removed from storage`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val sessionManager = SessionManager(settingsRepository)
        sessionManager.saveTokens("jwt-token", "refresh-token")

        // When
        sessionManager.clear()

        // Then
        assertNull(sessionManager.accessToken)
        assertNull(sessionManager.refreshToken)
        assertNull(settingsRepository.getToken())
        assertNull(settingsRepository.getRefreshToken())
    }
}
