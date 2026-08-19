package pt.socialfood.domain.usecase.restaurantvisitstatus

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetVisitStatusUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the status`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val repository = FakeRestaurantVisitStatusRepository(statusResult = Result.Success(status))
        val useCase = GetVisitStatusUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Success<VisitStatus?>>(result)
        assertEquals(status, result.data)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository = FakeRestaurantVisitStatusRepository(
            statusResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetVisitStatusUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
