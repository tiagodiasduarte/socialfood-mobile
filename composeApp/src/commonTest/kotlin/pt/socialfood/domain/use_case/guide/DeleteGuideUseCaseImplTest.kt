package pt.socialfood.domain.use_case.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeGuidesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DeleteGuideUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success and forwards the id`() = runTest {
        // Given
        val repository = FakeGuidesRepository(deleteResult = Result.Success(true))
        val useCase = DeleteGuideUseCaseImpl(repository)

        // When
        val result = useCase("guide-id")

        // Then
        assertEquals(Result.Success(true), result)
        assertEquals("guide-id", repository.lastDeleteId)
        assertEquals(1, repository.deleteInvokeCount)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository =
            FakeGuidesRepository(deleteResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = DeleteGuideUseCaseImpl(repository)

        // When
        val result = useCase("guide-id")

        // Then
        assertIs<Result.Failure>(result)
    }
}
