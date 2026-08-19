package pt.socialfood.domain.usecase.restaurantvisitstatus

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.RestaurantVisitStatus
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.random.nextEnum
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetRestaurantVisitStatusPagingUseCaseImplTest {
    @Test
    fun `given a status when invoked then forwards it and returns the repository's paging flow`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val pagingFlow = flowOf(PagingData.empty<RestaurantVisitStatus>())
        val repository = FakeRestaurantVisitStatusRepository(pagingFlow = pagingFlow)
        val useCase = GetRestaurantVisitStatusPagingUseCaseImpl(repository)

        // When
        val flow = useCase(status)

        // Then
        assertSame(pagingFlow, flow)
        assertEquals(status, repository.lastStatus)
    }
}
