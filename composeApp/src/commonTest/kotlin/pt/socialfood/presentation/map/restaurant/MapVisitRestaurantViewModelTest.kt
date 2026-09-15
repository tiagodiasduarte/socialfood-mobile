package pt.socialfood.presentation.map.restaurant

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeGetRestaurantVisitStatusListUseCase
import pt.socialfood.random.nextEnum
import pt.socialfood.random.nextRestaurant
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class MapVisitRestaurantViewModelTest {
    @Test
    fun `given the use case emits restaurants when created then restaurants reflects them`() =
        runTestWithMainDispatcher {
            // Given
            val status = Random.nextEnum<VisitStatus>()
            val restaurants = listOf(Random.nextRestaurant(), Random.nextRestaurant())
            val useCase = FakeGetRestaurantVisitStatusListUseCase { flowOf(restaurants) }

            // When
            val vm = MapVisitRestaurantViewModel(getRestaurantVisitStatusList = useCase, status = status)

            // Then
            vm.restaurants.test {
                assertEquals(emptyList(), awaitItem())
                assertEquals(restaurants, awaitItem())
            }
            assertEquals(status, useCase.lastStatus)
        }
}
