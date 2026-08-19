package pt.socialfood.domain.usecase.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.random.nextPagedGuides
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FindGuidesUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards page limit query and userId`() = runTest {
        // Given
        val paged = Random.nextPagedGuides()
        val page = Random.nextInt(1, 10)
        val limit = Random.nextInt(10, 50)
        val query = Random.nextString()
        val userId = Random.nextString()
        val repository = FakeGuidesRepository(findGuidesPagedResult = Result.Success(paged))
        val useCase = FindGuidesUseCaseImpl(repository)

        // When
        val result = useCase(page = page, limit = limit, query = query, userId = userId)

        // Then
        assertEquals(Result.Success(paged), result)
        assertEquals(page, repository.lastFindGuidesPagedPage)
        assertEquals(limit, repository.lastFindGuidesPagedLimit)
        assertEquals(query, repository.lastFindGuidesPagedQuery)
        assertEquals(userId, repository.lastFindGuidesPagedUserId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeGuidesRepository(
            findGuidesPagedResult = Result.Failure(
                DataError.Network(Exception("test error")),
            ),
        )
        val useCase = FindGuidesUseCaseImpl(repository)

        // When
        val result = useCase(page = Random.nextInt(1, 10), limit = Random.nextInt(10, 50))

        // Then
        assertIs<Result.Failure>(result)
    }
}
