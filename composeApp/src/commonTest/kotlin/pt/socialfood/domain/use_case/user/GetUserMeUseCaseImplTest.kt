package pt.socialfood.domain.use_case.user

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextUser
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetUserMeUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the current user`() = runTest {
        // Given
        val user = Random.nextUser()
        val repository = FakeUsersRepository(getUserMeResult = Result.Success(user))
        val useCase = GetUserMeUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.Success(user), result)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeUsersRepository(
            getUserMeResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetUserMeUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
