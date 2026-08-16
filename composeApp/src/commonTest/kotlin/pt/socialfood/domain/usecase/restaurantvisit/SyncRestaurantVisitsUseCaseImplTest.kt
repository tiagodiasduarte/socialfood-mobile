package pt.socialfood.domain.usecase.restaurantvisit

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitsRepository
import pt.socialfood.random.nextEnum
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class SyncRestaurantVisitsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then delegates status and returns Success`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val repository = FakeRestaurantVisitsRepository(syncResult = Result.Success(Unit))
        val useCase = SyncRestaurantVisitsUseCaseImpl(repository)

        // When
        val result = useCase(status)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(status, repository.lastStatus)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeRestaurantVisitsRepository(syncResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = SyncRestaurantVisitsUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextEnum())

        // Then
        assertIs<Result.Failure>(result)
    }
}
