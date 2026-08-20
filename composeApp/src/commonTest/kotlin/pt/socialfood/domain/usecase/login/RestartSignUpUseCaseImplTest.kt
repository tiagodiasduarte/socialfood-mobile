package pt.socialfood.domain.usecase.login

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNull

class RestartSignUpUseCaseImplTest {
    @Test
    @Suppress("MaxLineLength", "ktlint:standard:max-line-length")
    fun `given a pending verification email when invoked then pendingVerificationEmail is cleared and returns Success`() =
        runTest {
            // Given
            val settingsRepository = FakeSettingsRepository()
            settingsRepository.savePendingVerificationEmail("user@test.com")
            val useCase = RestartSignUpUseCaseImpl(settingsRepository)

            // When
            val result = useCase.invoke()

            // Then
            assertIs<Result.Success<Boolean>>(result)
            assertNull(settingsRepository.getPendingVerificationEmail())
        }
}
