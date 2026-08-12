package pt.socialfood.domain.usecase.user

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextString
import pt.socialfood.random.nextUser
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetUserByIdUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the user and forwards the id`() = runTest {
        // Given
        val user = Random.nextUser()
        val repository = FakeUsersRepository(findByIdResult = Result.Success(user))
        val useCase = GetUserByIdUseCaseImpl(repository)

        // When
        val result = useCase(user.id)

        // Then
        assertEquals(Result.Success(user), result)
        assertEquals(user.id, repository.lastFindByIdId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeUsersRepository(
            findByIdResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetUserByIdUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
