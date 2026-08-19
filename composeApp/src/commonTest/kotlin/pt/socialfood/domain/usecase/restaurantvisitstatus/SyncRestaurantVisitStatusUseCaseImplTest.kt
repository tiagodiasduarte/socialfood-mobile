package pt.socialfood.domain.usecase.restaurantvisitstatus

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import kotlin.test.Test
import kotlin.test.assertIs

class SyncRestaurantVisitStatusUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success`() = runTest {
        // Given
        val repository = FakeRestaurantVisitStatusRepository(syncResult = Result.Success(Unit))
        val useCase = SyncRestaurantVisitStatusUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeRestaurantVisitStatusRepository(syncResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = SyncRestaurantVisitStatusUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
