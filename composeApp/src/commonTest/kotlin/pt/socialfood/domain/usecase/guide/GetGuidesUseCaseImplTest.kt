package pt.socialfood.domain.usecase.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.random.nextGuide
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetGuidesUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns its guides`() = runTest {
        // Given
        val guides = listOf(Random.nextGuide(), Random.nextGuide())
        val repository = FakeGuidesRepository(findGuidesResult = Result.Success(guides))
        val useCase = GetGuidesUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.Success(guides), result)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeGuidesRepository(
            findGuidesResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetGuidesUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
