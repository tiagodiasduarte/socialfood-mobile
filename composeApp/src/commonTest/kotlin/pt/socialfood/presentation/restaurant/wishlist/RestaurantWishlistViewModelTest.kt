package pt.socialfood.presentation.restaurant.wishlist

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.fakes.FakeGetRestaurantVisitStatusPagingUseCase
import pt.socialfood.fakes.FakeMarkRestaurantVisitStatusUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantVisitStatusUseCase
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
            val pagingUseCase = FakeGetRestaurantVisitStatusPagingUseCase()
            val vm = RestaurantWishlistViewModel(
                pagingUseCase,
                FakeMarkRestaurantVisitStatusUseCase(),
                FakeUnmarkRestaurantVisitStatusUseCase(),
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            val job = launch { vm.restaurants.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(VisitStatus.WISHLIST, pagingUseCase.lastStatus)
            job.cancel()
        }

    @Test
    fun `given a restaurant when addToWishlist is called then marks it as WISHLIST`() = runTestWithMainDispatcher {
        // Given
        val markUseCase = FakeMarkRestaurantVisitStatusUseCase()
        val vm = RestaurantWishlistViewModel(
            FakeGetRestaurantVisitStatusPagingUseCase(),
            markUseCase,
            FakeUnmarkRestaurantVisitStatusUseCase(),
            FakeObserveUserUseCase(Random.nextUser()),
        )
        val restaurant = Random.nextRestaurant()

        // When
        vm.addToWishlist(restaurant)
        advanceUntilIdle()

        // Then
        assertEquals(restaurant, markUseCase.lastMarkedRestaurant)
        assertEquals(VisitStatus.WISHLIST, markUseCase.lastStatus)
    }

    @Test
    fun `given a restaurant id when removeFromWishlist is called then unmarks it as WISHLIST`() =
        runTestWithMainDispatcher {
            // Given
            val unmarkUseCase = FakeUnmarkRestaurantVisitStatusUseCase()
            val vm = RestaurantWishlistViewModel(
                FakeGetRestaurantVisitStatusPagingUseCase(),
                FakeMarkRestaurantVisitStatusUseCase(),
                unmarkUseCase,
                FakeObserveUserUseCase(Random.nextUser()),
            )

            // When
            vm.removeFromWishlist("r1")
            advanceUntilIdle()

            // Then
            assertEquals("r1", unmarkUseCase.lastUnmarkedRestaurantId)
            assertEquals(VisitStatus.WISHLIST, unmarkUseCase.lastStatus)
        }
}
