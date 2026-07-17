package pt.socialfood.data.network

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SessionManagerTest {

    @Test
    fun `given no pending verification saved when session manager is created then pendingVerificationEmail is null`() {
        // Given
        val settings = MapSettings()

        // When
        val sessionManager = SessionManager(settings)

        // Then
        assertNull(sessionManager.pendingVerificationEmail)
    }

    @Test
    fun `given an email when savePendingVerification is called then pendingVerificationEmail is set and persisted`() {
        // Given
        val settings = MapSettings()
        val sessionManager = SessionManager(settings)

        // When
        sessionManager.savePendingVerification("user@test.com")

        // Then
        assertEquals("user@test.com", sessionManager.pendingVerificationEmail)
        assertEquals("user@test.com", settings.getStringOrNull("pending_verification_email"))
    }

    @Test
    fun `given a persisted pending email when a new SessionManager is created then pendingVerificationEmail is reloaded`() {
        // Given
        val settings = MapSettings()
        SessionManager(settings).savePendingVerification("user@test.com")

        // When
        val reloaded = SessionManager(settings)

        // Then
        assertEquals("user@test.com", reloaded.pendingVerificationEmail)
    }

    @Test
    fun `given a saved pending email when clearPendingVerification is called then pendingVerificationEmail is cleared and removed from storage`() {
        // Given
        val settings = MapSettings()
        val sessionManager = SessionManager(settings)
        sessionManager.savePendingVerification("user@test.com")

        // When
        sessionManager.clearPendingVerification()

        // Then
        assertNull(sessionManager.pendingVerificationEmail)
        assertNull(settings.getStringOrNull("pending_verification_email"))
    }
}
