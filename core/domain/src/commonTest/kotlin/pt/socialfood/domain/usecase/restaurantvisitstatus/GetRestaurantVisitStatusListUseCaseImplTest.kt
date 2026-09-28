package pt.socialfood.domain.usecase.restaurantvisitstatus

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextRestaurant
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetRestaurantVisitStatusListUseCaseImplTest {
    @Test
    fun `given a status when invoked then forwards it and returns the repository's list flow`() = runTest {
        // Given
        val status = Random.nextEnum<VisitStatus>()
        val listFlow = flowOf(listOf(Random.nextRestaurant()))
        val repository = FakeRestaurantVisitStatusRepository(allFlow = listFlow)
        val useCase = GetRestaurantVisitStatusListUseCaseImpl(repository)

        // When
        val flow = useCase(status)

        // Then
        assertSame(listFlow, flow)
        assertEquals(status, repository.lastStatus)
    }
}
