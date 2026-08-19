package pt.socialfood.presentation.restaurant.visited

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeGetRestaurantVisitStatusPagingUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantVisitStatusUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantVisitedViewModelTest {

    @Test
    fun `given the view model is created then requests the paging flow scoped to VISITED`() =
        runTestWithMainDispatcher {
            // Given
            val pagingUseCase = FakeGetRestaurantVisitStatusPagingUseCase()

            // When
            RestaurantVisitedViewModel(pagingUseCase, FakeUnmarkRestaurantVisitStatusUseCase())

            // Then
            assertEquals(VisitStatus.VISITED, pagingUseCase.lastStatus)
        }

    @Test
    fun `given a restaurant id when removeFromVisited is called then unmarks it as VISITED`() =
        runTestWithMainDispatcher {
            // Given
            val unmarkUseCase = FakeUnmarkRestaurantVisitStatusUseCase()
            val vm = RestaurantVisitedViewModel(FakeGetRestaurantVisitStatusPagingUseCase(), unmarkUseCase)

            // When
            vm.removeFromVisited("r1")
            advanceUntilIdle()

            // Then
            assertEquals("r1", unmarkUseCase.lastUnmarkedRestaurantId)
            assertEquals(VisitStatus.VISITED, unmarkUseCase.lastStatus)
        }
}
