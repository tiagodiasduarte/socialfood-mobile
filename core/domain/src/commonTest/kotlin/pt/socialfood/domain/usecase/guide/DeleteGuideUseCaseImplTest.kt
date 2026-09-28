package pt.socialfood.domain.usecase.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DeleteGuideUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success and forwards the id`() = runTest {
        // Given
        val id = Random.nextString()
        val repository = FakeGuidesRepository(deleteResult = Result.Success(true))
        val useCase = DeleteGuideUseCaseImpl(repository)

        // When
        val result = useCase(id)

        // Then
        assertEquals(Result.Success(true), result)
        assertEquals(id, repository.lastDeleteId)
        assertEquals(1, repository.deleteInvokeCount)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeGuidesRepository(deleteResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = DeleteGuideUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
