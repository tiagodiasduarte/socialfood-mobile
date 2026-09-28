package pt.socialfood.presentation.favourite.restaurant

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeGetFavouriteRestaurantsPagingUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantFavouriteUseCase
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteRestaurantsViewModelTest {

    @Test
    fun `given the current user is available when restaurants is collected then requests the paging flow`() =
        runTestWithMainDispatcher {
            // Given
            val pagingUseCase = FakeGetFavouriteRestaurantsPagingUseCase()
            val vm = FavouriteRestaurantsViewModel(
                pagingUseCase,
                FakeUnmarkRestaurantFavouriteUseCase(),
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            val job = launch { vm.restaurants.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, pagingUseCase.invokeCount)
            job.cancel()
        }

    @Test
    fun `given a restaurant id when removeFavourite is called then unmarks it`() = runTestWithMainDispatcher {
        // Given
        val unmarkUseCase = FakeUnmarkRestaurantFavouriteUseCase()
        val vm = FavouriteRestaurantsViewModel(
            FakeGetFavouriteRestaurantsPagingUseCase(),
            unmarkUseCase,
            FakeObserveUserUseCase(Random.nextUser()),
        )

        // When
        vm.removeFavourite("r1")
        advanceUntilIdle()

        // Then
        assertEquals(1, unmarkUseCase.invokeCount)
        assertEquals("r1", unmarkUseCase.lastRestaurantId)
    }
}
