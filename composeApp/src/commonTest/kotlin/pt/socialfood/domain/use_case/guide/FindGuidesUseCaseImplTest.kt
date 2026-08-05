package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.random.nextPagedGuides
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FindGuidesUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then forwards page, limit, query and userId`() = runTest {
        // Given
        val paged = Random.nextPagedGuides()
        val repository = FakeGuidesRepository(findGuidesPagedResult = Result.Success(paged))
        val useCase = FindGuidesUseCaseImpl(repository)

        // When
        val result = useCase(page = 2, limit = 20, query = "pizza", userId = "author-id")

        // Then
        assertEquals(Result.Success(paged), result)
        assertEquals(2, repository.lastFindGuidesPagedPage)
        assertEquals(20, repository.lastFindGuidesPagedLimit)
        assertEquals("pizza", repository.lastFindGuidesPagedQuery)
        assertEquals("author-id", repository.lastFindGuidesPagedUserId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository =
            FakeGuidesRepository(findGuidesPagedResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = FindGuidesUseCaseImpl(repository)

        // When
        val result = useCase(page = 1, limit = 10)

        // Then
        assertIs<Result.Failure>(result)
    }
}
