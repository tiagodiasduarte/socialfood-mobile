package pt.socialfood.domain.use_case.login

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeAuthRepository
import pt.socialfood.fakes.FakeSettingsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class RegisterUseCaseImplTest {
    @Test
    fun `given successful register when invoked then pendingVerificationEmail is persisted and returns Success`() =
        runTest {
            // Given
            val settingsRepository = FakeSettingsRepository()
            val fakeRepo =
                FakeAuthRepository(
                    loginResult = Result.Success("token"),
                    registerResult = Result.Success(Unit),
                )
            val useCase = RegisterUseCaseImpl(fakeRepo, settingsRepository)

            // When
            val result = useCase.invoke("John Doe", "john.doe@test.com", "password")

            // Then
            assertIs<Result.Success<Boolean>>(result)
            assertEquals("john.doe@test.com", settingsRepository.getPendingVerificationEmail())
        }

    @Test
    fun `given failed register when invoked then pendingVerificationEmail is untouched and returns Error`() =
        runTest {
            // Given
            val settingsRepository = FakeSettingsRepository()
            val fakeRepo =
                FakeAuthRepository(
                    loginResult = Result.Success("token"),
                    registerResult = Result.Failure(DataError.Network(Exception("test error"))),
                )
            val useCase = RegisterUseCaseImpl(fakeRepo, settingsRepository)

            // When
            val result = useCase.invoke("John Doe", "john.doe@test.com", "password")

            // Then
            assertIs<Result.Failure>(result)
            assertNull(settingsRepository.getPendingVerificationEmail())
        }
}
