package pt.socialfood.presentation.favourite.restaurant

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import pt.socialfood.fakes.FakeObserveUserUseCase
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
            val favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository()
            val vm = FavouriteRestaurantsViewModel(
                favouriteRestaurantsRepository,
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            val job = launch { vm.restaurants.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, favouriteRestaurantsRepository.pagingInvokeCount)
            job.cancel()
        }

    @Test
    fun `given a restaurant id when removeFavourite is called then unmarks it`() = runTestWithMainDispatcher {
        // Given
        val favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository()
        val vm = FavouriteRestaurantsViewModel(
            favouriteRestaurantsRepository,
            FakeObserveUserUseCase(Random.nextUser()),
        )

        // When
        vm.removeFavourite("r1")
        advanceUntilIdle()

        // Then
        assertEquals(1, favouriteRestaurantsRepository.unmarkInvokeCount)
        assertEquals("r1", favouriteRestaurantsRepository.lastUnmarkedRestaurantId)
    }
}
