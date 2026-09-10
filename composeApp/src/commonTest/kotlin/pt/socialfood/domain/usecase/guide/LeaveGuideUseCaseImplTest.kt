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

class LeaveGuideUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success and forwards the guideId`() = runTest {
        // Given
        val guideId = Random.nextString()
        val repository = FakeGuidesRepository(leaveGuideResult = Result.Success(true))
        val useCase = LeaveGuideUseCaseImpl(repository)

        // When
        val result = useCase(guideId)

        // Then
        assertEquals(Result.Success(true), result)
        assertEquals(guideId, repository.lastLeaveGuideId)
        assertEquals(1, repository.leaveGuideInvokeCount)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val error = DataError.Network(Exception("test error"))
        val repository = FakeGuidesRepository(leaveGuideResult = Result.Failure(error))
        val useCase = LeaveGuideUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
