package pt.socialfood.domain.usecase.restaurantvisitstatus

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.PagedRestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.random.nextEnum
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetRestaurantVisitStatusUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then delegates status and returns Success`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val repository =
            FakeRestaurantVisitStatusRepository(
                pagedResult =
                Result.Success(
                    PagedRestaurantVisitStatus(visits = emptyList(), page = 1, total = 0, hasMore = false),
                ),
            )
        val useCase = GetRestaurantVisitStatusUseCaseImpl(repository)

        // When
        val result = useCase(status = status, page = 1, limit = 10)

        // Then
        assertIs<Result.Success<PagedRestaurantVisitStatus>>(result)
        assertEquals(status, repository.lastStatus)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeRestaurantVisitStatusRepository(
                pagedResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
        val useCase = GetRestaurantVisitStatusUseCaseImpl(repository)

        // When
        val result = useCase(status = Random.nextEnum(), page = 1, limit = 10)

        // Then
        assertIs<Result.Failure>(result)
    }
}
