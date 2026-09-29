package pt.socialfood.presentation.map.restaurant.visitstatus

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextRestaurant
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MapRestaurantVisitStatusViewModelTest {
    @Test
    fun `given the repository emits restaurants when created then restaurants reflects them`() =
        runTestWithMainDispatcher {
            // Given
            val status = Random.nextEnum<VisitStatus>()
            val restaurants = listOf(Random.nextRestaurant(), Random.nextRestaurant())
            val visitStatusRepository = FakeRestaurantVisitStatusRepository(allFlow = { flowOf(restaurants) })

            // When
            val vm = MapRestaurantVisitStatusViewModel(
                restaurantVisitStatusRepository = visitStatusRepository,
                status = status,
            )

            // Then
            vm.restaurants.test {
                assertEquals(emptyList(), awaitItem())
                assertEquals(restaurants, awaitItem())
            }
            assertEquals(status, visitStatusRepository.lastAllFlowStatus)
        }
}
