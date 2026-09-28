package pt.socialfood.domain.usecase.user

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeUsersRepository
import pt.socialfood.random.nextPagedUsers
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FindUsersUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards page limit and query`() = runTest {
        // Given
        val paged = Random.nextPagedUsers()
        val page = Random.nextInt(1, 10)
        val limit = Random.nextInt(10, 50)
        val query = Random.nextString()
        val repository = FakeUsersRepository(findUsersResult = Result.Success(paged))
        val useCase = FindUsersUseCaseImpl(repository)

        // When
        val result = useCase(page = page, limit = limit, query = query)

        // Then
        assertEquals(Result.Success(paged), result)
        assertEquals(page, repository.lastFindUsersPage)
        assertEquals(limit, repository.lastFindUsersLimit)
        assertEquals(query, repository.lastFindUsersQuery)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeUsersRepository(
            findUsersResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = FindUsersUseCaseImpl(repository)

        // When
        val result = useCase(page = Random.nextInt(1, 10), limit = Random.nextInt(10, 50))

        // Then
        assertIs<Result.Failure>(result)
    }
}
