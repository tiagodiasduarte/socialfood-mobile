package pt.socialfood.data.repository

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AuthRepositoryImplTest {

    private fun createRepository(shouldThrow: Boolean = false): AuthRepositoryImpl =
        AuthRepositoryImpl(FakeAuthApi(shouldThrow))

    @Test
    fun `given valid credentials when login is called then returns Success with token`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.login("user@test.com", "password")

        // Then
        assertIs<Result.Success<String>>(result)
        assertEquals("token", result.data)
    }

    @Test
    fun `given api throws when login is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.login("user@test.com", "password")

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    @Test
    fun `given valid data when register is called then returns Success`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.register("name", "user@test.com", "password")

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given api throws when register is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.register("name", "user@test.com", "password")

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    @Test
    fun `given valid token when validateCode is called then returns Success with new token`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.validateCode("user@test.com", "sometoken")

        // Then
        assertIs<Result.Success<String>>(result)
        assertEquals("newtoken", result.data)
    }

    @Test
    fun `given api throws when validateCode is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.validateCode("user@test.com", "sometoken")

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    @Test
    fun `given valid email when resendVerificationCode is called then returns Success`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.resendVerificationCode("user@test.com")

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given api throws when resendVerificationCode is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.resendVerificationCode("user@test.com")

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    @Test
    fun `given valid id token when loginWithGoogle is called then returns Success with token`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.loginWithGoogle("google_id_token")

        // Then
        assertIs<Result.Success<String>>(result)
        assertEquals("token", result.data)
    }

    @Test
    fun `given api throws when loginWithGoogle is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.loginWithGoogle("google_id_token")

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }

    @Test
    fun `given authenticated session when logout is called then returns Success`() = runTest {
        // Given
        val repo = createRepository()

        // When
        val result = repo.logout()

        // Then
        assertIs<Result.Success<Boolean>>(result)
        assertEquals(true, result.data)
    }

    @Test
    fun `given api throws when logout is called then returns Error Unknown`() = runTest {
        // Given
        val repo = createRepository(shouldThrow = true)

        // When
        val result = repo.logout()

        // Then
        assertIs<Result.Failure>(result)
        assertIs<DataError.Network>(result.error)
    }
}
