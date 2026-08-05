package pt.socialfood.domain.use_case.home

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeHomeRepository
import pt.socialfood.random.nextHomeSection
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetHomeSectionsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns its sections`() = runTest {
        // Given
        val sections = listOf(Random.nextHomeSection(), Random.nextHomeSection())
        val repository = FakeHomeRepository(findAllResult = Result.Success(sections))
        val useCase = GetHomeSectionsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.Success(sections), result)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeHomeRepository(
            findAllResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetHomeSectionsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
