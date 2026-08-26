package pt.socialfood.presentation.favourite

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeGetFavouriteGuidesPagingUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.presentation.favourite.guide.FavouriteGuidesViewModel
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteGuidesViewModelTest {

    @Test
    fun `given the view model is created then requests the favourite guides paging flow`() = runTestWithMainDispatcher {
        // Given
        val pagingUseCase = FakeGetFavouriteGuidesPagingUseCase()

        // When
        FavouriteGuidesViewModel(pagingUseCase, FakeUnmarkGuideFavouriteUseCase())

        // Then
        assertEquals(1, pagingUseCase.invokeCount)
    }

    @Test
    fun `given a guide id when removeFavourite is called then unmarks it`() = runTestWithMainDispatcher {
        // Given
        val unmarkUseCase = FakeUnmarkGuideFavouriteUseCase()
        val vm = FavouriteGuidesViewModel(FakeGetFavouriteGuidesPagingUseCase(), unmarkUseCase)

        // When
        vm.removeFavourite("g1")
        advanceUntilIdle()

        // Then
        assertEquals(1, unmarkUseCase.invokeCount)
        assertEquals("g1", unmarkUseCase.lastGuideId)
    }
}
