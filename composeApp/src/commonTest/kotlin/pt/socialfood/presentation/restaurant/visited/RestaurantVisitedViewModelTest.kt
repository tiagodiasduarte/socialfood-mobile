package pt.socialfood.presentation.restaurant.visited

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeGetRestaurantVisitStatusPagingUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantVisitStatusUseCase
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
            val pagingUseCase = FakeGetRestaurantVisitStatusPagingUseCase()
            val vm = RestaurantVisitedViewModel(
                pagingUseCase,
                FakeUnmarkRestaurantVisitStatusUseCase(),
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            val job = launch { vm.restaurants.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(VisitStatus.VISITED, pagingUseCase.lastStatus)
            job.cancel()
        }

    @Test
    fun `given a restaurant id when removeFromVisited is called then unmarks it as VISITED`() =
        runTestWithMainDispatcher {
            // Given
            val unmarkUseCase = FakeUnmarkRestaurantVisitStatusUseCase()
            val vm = RestaurantVisitedViewModel(
                FakeGetRestaurantVisitStatusPagingUseCase(),
                unmarkUseCase,
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            vm.removeFromVisited("r1")
            advanceUntilIdle()

            // Then
            assertEquals("r1", unmarkUseCase.lastUnmarkedRestaurantId)
            assertEquals(VisitStatus.VISITED, unmarkUseCase.lastStatus)
        }
}
