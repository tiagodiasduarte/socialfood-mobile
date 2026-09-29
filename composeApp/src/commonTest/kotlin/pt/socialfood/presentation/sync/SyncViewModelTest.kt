package pt.socialfood.presentation.sync

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeConnectivityObserver
import pt.socialfood.fakes.FakeRestaurantVisitStatusRepository
import pt.socialfood.fakes.FakeSyncFavouriteRestaurantsUseCase
import pt.socialfood.fakes.FakeSyncFavouritesUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SyncViewModelTest {
    @Test
    fun `given a fresh view model when onStart is called then all sync operations run once`() =
        runTestWithMainDispatcher {
            // Given
            val syncFavourites = FakeSyncFavouritesUseCase()
            val syncFavouriteRestaurants = FakeSyncFavouriteRestaurantsUseCase()
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()
            val vm = SyncViewModel(
                syncFavourites,
                syncFavouriteRestaurants,
                visitStatusRepository,
                FakeConnectivityObserver(),
            )

            // When
            vm.onStart()
            advanceUntilIdle()

            // Then
            assertEquals(1, syncFavourites.invokeCount)
            assertEquals(1, syncFavouriteRestaurants.invokeCount)
            assertEquals(1, visitStatusRepository.syncInvokeCount)
        }

    @Test
    fun `given the connectivity observer starts online when view model is created then no sync runs`() =
        runTestWithMainDispatcher {
            // Given
            val syncFavourites = FakeSyncFavouritesUseCase()
            val syncFavouriteRestaurants = FakeSyncFavouriteRestaurantsUseCase()
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()

            // When
            SyncViewModel(
                syncFavourites,
                syncFavouriteRestaurants,
                visitStatusRepository,
                FakeConnectivityObserver(initiallyOnline = true),
            )
            advanceUntilIdle()

            // Then
            assertEquals(0, syncFavourites.invokeCount)
            assertEquals(0, syncFavouriteRestaurants.invokeCount)
            assertEquals(0, visitStatusRepository.syncInvokeCount)
        }

    @Test
    fun `given connectivity drops then comes back when observed then all sync operations run once`() =
        runTestWithMainDispatcher {
            // Given
            val syncFavourites = FakeSyncFavouritesUseCase()
            val syncFavouriteRestaurants = FakeSyncFavouriteRestaurantsUseCase()
            val visitStatusRepository = FakeRestaurantVisitStatusRepository()
            val connectivityObserver = FakeConnectivityObserver(initiallyOnline = true)
            SyncViewModel(
                syncFavourites,
                syncFavouriteRestaurants,
                visitStatusRepository,
                connectivityObserver,
            )
            advanceUntilIdle()

            // When
            connectivityObserver.setOnline(false)
            advanceUntilIdle()
            connectivityObserver.setOnline(true)
            advanceUntilIdle()

            // Then
            assertEquals(1, syncFavourites.invokeCount)
            assertEquals(1, syncFavouriteRestaurants.invokeCount)
            assertEquals(1, visitStatusRepository.syncInvokeCount)
        }
}
