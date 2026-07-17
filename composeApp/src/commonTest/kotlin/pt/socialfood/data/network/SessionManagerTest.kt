package pt.socialfood.data.network

import kotlinx.coroutines.test.runTest
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionManagerTest {

    @Test
    fun `given no token saved when session manager is created then token is null`() {
        // Given
        val settingsRepository = FakeSettingsRepository()

        // When
        val sessionManager = SessionManager(settingsRepository)

        // Then
        assertNull(sessionManager.token)
    }

    @Test
    fun `given a token when saveToken is called then token is set and persisted`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val sessionManager = SessionManager(settingsRepository)

        // When
        sessionManager.saveToken("jwt-token")

        // Then
        assertEquals("jwt-token", sessionManager.token)
        assertEquals("jwt-token", settingsRepository.getToken())
    }

    @Test
    fun `given a persisted token when a new SessionManager is created then token is reloaded`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        SessionManager(settingsRepository).saveToken("jwt-token")

        // When
        val reloaded = SessionManager(settingsRepository)

        // Then
        assertEquals("jwt-token", reloaded.token)
    }

    @Test
    fun `given a saved token when clear is called then token is cleared and removed from storage`() = runTest {
        // Given
        val settingsRepository = FakeSettingsRepository()
        val sessionManager = SessionManager(settingsRepository)
        sessionManager.saveToken("jwt-token")

        // When
        sessionManager.clear()

        // Then
        assertNull(sessionManager.token)
        assertNull(settingsRepository.getToken())
    }
}
