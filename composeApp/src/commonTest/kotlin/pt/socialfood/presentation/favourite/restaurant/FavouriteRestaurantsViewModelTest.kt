package pt.socialfood.presentation.favourite.restaurant

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeGetFavouriteRestaurantsPagingUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantFavouriteUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteRestaurantsViewModelTest {

    @Test
    fun `given the view model is created then requests the favourite restaurants paging flow`() =
        runTestWithMainDispatcher {
            // Given
            val pagingUseCase = FakeGetFavouriteRestaurantsPagingUseCase()

            // When
            FavouriteRestaurantsViewModel(pagingUseCase, FakeUnmarkRestaurantFavouriteUseCase())

            // Then
            assertEquals(1, pagingUseCase.invokeCount)
        }

    @Test
    fun `given a restaurant id when removeFavourite is called then unmarks it`() = runTestWithMainDispatcher {
        // Given
        val unmarkUseCase = FakeUnmarkRestaurantFavouriteUseCase()
        val vm = FavouriteRestaurantsViewModel(FakeGetFavouriteRestaurantsPagingUseCase(), unmarkUseCase)

        // When
        vm.removeFavourite("r1")
        advanceUntilIdle()

        // Then
        assertEquals(1, unmarkUseCase.invokeCount)
        assertEquals("r1", unmarkUseCase.lastRestaurantId)
    }
}
