package pt.socialfood.presentation.favourite

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeGetFavouriteGuidesPagingUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesViewModel
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteGuidesViewModelTest {

    @Test
    fun `given the current user is available when guides is collected then requests the paging flow`() =
        runTestWithMainDispatcher {
            // Given
            val pagingUseCase = FakeGetFavouriteGuidesPagingUseCase()
            val vm = FavouriteGuidesViewModel(
                pagingUseCase,
                FakeUnmarkGuideFavouriteUseCase(),
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            val job = launch { vm.guides.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, pagingUseCase.invokeCount)
            job.cancel()
        }

    @Test
    fun `given a guide id when removeFavourite is called then unmarks it`() = runTestWithMainDispatcher {
        // Given
        val unmarkUseCase = FakeUnmarkGuideFavouriteUseCase()
        val vm = FavouriteGuidesViewModel(
            FakeGetFavouriteGuidesPagingUseCase(),
            unmarkUseCase,
            FakeObserveUserUseCase(Random.nextUser()),
        )

        // When
        vm.removeFavourite("g1")
        advanceUntilIdle()

        // Then
        assertEquals(1, unmarkUseCase.invokeCount)
        assertEquals("g1", unmarkUseCase.lastGuideId)
    }
}
