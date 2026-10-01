package pt.socialfood.presentation.restaurant.visited

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.presentation.restaurant.navigation.RestaurantPickerResultsImpl
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantVisitedViewModelTest {

    @Test
    fun `given the current user is available when restaurants is collected then requests VISITED paging flow`() =
        runTestWithMainDispatcher {
            // Given
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()
            val vm = RestaurantVisitedViewModel(
                visitStatusRepository,
                FakeObserveUserUseCase(Random.nextUser()),
                RestaurantPickerResultsImpl(),
            )

            // When
            val job = launch { vm.restaurants.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(VisitStatus.VISITED, visitStatusRepository.lastPagingStatus)
            job.cancel()
        }

    @Test
    fun `given a restaurant when it is picked then marks it as VISITED`() = runTestWithMainDispatcher {
        // Given
        val visitStatusRepository = FakeRestaurantVisitStatusRepository()
        val pickerResults = RestaurantPickerResultsImpl()
        val vm = RestaurantVisitedViewModel(
            visitStatusRepository,
            FakeObserveUserUseCase(Random.nextUser()),
            pickerResults,
        )
        val restaurant = Random.nextRestaurant()

        // When
        pickerResults.publish(vm.restaurantPickerKey, restaurant)
        advanceUntilIdle()

        // Then
        assertEquals(restaurant, visitStatusRepository.lastMarkedRestaurant)
        assertEquals(VisitStatus.VISITED, visitStatusRepository.lastMarkedStatus)
    }

    @Test
    fun `given a restaurant id when removeFromVisited is called then unmarks it as VISITED`() =
        runTestWithMainDispatcher {
            // Given
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()
            val vm = RestaurantVisitedViewModel(
                visitStatusRepository,
                FakeObserveUserUseCase(Random.nextUser()),
                RestaurantPickerResultsImpl(),
            )

            // When
            vm.removeFromVisited("r1")
            advanceUntilIdle()

            // Then
            assertEquals("r1", visitStatusRepository.lastUnmarkedRestaurantId)
            assertEquals(VisitStatus.VISITED, visitStatusRepository.lastUnmarkedStatus)
        }
}
