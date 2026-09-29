package pt.socialfood.presentation.restaurant.wishlist

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantWishlistViewModelTest {

    @Test
    fun `given the current user is available when restaurants is collected then requests WISHLIST paging flow`() =
        runTestWithMainDispatcher {
            // Given
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()
            val vm = RestaurantWishlistViewModel(
                visitStatusRepository,
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            val job = launch { vm.restaurants.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(VisitStatus.WISHLIST, visitStatusRepository.lastPagingStatus)
            job.cancel()
        }

    @Test
    fun `given a restaurant when addToWishlist is called then marks it as WISHLIST`() = runTestWithMainDispatcher {
        // Given
        val visitStatusRepository = FakeRestaurantVisitStatusRepository()
        val vm = RestaurantWishlistViewModel(
            visitStatusRepository,
            FakeObserveUserUseCase(Random.nextUser()),
        )
        val restaurant = Random.nextRestaurant()

        // When
        vm.addToWishlist(restaurant)
        advanceUntilIdle()

        // Then
        assertEquals(restaurant, visitStatusRepository.lastMarkedRestaurant)
        assertEquals(VisitStatus.WISHLIST, visitStatusRepository.lastMarkedStatus)
    }

    @Test
    fun `given a restaurant id when removeFromWishlist is called then unmarks it as WISHLIST`() =
        runTestWithMainDispatcher {
            // Given
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()
            val vm = RestaurantWishlistViewModel(
                visitStatusRepository,
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            vm.removeFromWishlist("r1")
            advanceUntilIdle()

            // Then
            assertEquals("r1", visitStatusRepository.lastUnmarkedRestaurantId)
            assertEquals(VisitStatus.WISHLIST, visitStatusRepository.lastUnmarkedStatus)
        }
}
