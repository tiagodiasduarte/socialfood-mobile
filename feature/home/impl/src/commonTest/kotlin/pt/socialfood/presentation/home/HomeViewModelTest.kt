package pt.socialfood.presentation.home

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.HomeItemType
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.HomeSectionType
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.FavouritesGuidesRepository
import pt.socialfood.domain.repository.HomeRepository
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import pt.socialfood.fakes.FakeFavouritesGuidesRepository
import pt.socialfood.fakes.FakeHomeRepository
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextHomeSection
import pt.socialfood.random.nextHomeSectionItem
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private fun homeSection(id: String) = HomeSection(
        id = id,
        title = "Section $id",
        type = HomeSectionType.RESTAURANT_LIST,
        position = 0,
        isActive = true,
    )

    private fun createViewModel(
        homeRepository: HomeRepository = FakeHomeRepository(),
        favouriteRestaurantsRepository: FavouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(),
        favouritesGuidesRepository: FavouritesGuidesRepository = FakeFavouritesGuidesRepository(),
        observeUser: ObserveUserUseCase = FakeObserveUserUseCase(Random.nextUser()),
    ) = HomeViewModel(
        homeRepository,
        favouriteRestaurantsRepository,
        favouritesGuidesRepository,
        observeUser,
    )

    @Test
    fun `given the current user is observed then user reflects the emitted value`() = runTestWithMainDispatcher {
        // Given
        val currentUser = Random.nextUser()
        val observeUser = FakeObserveUserUseCase(initial = currentUser)

        // When / Then
        val vm = createViewModel(observeUser = observeUser)
        vm.user.test {
            awaitItem()
            assertEquals(currentUser, awaitItem())
        }
    }

    @Test
    fun `given the current user changes when a new value is emitted then user updates`() = runTestWithMainDispatcher {
        // Given
        val observeUser = FakeObserveUserUseCase()
        val vm = createViewModel(observeUser = observeUser)

        vm.user.test {
            awaitItem()

            // When
            val updated = Random.nextUser()
            observeUser.emit(updated)

            // Then
            assertEquals(updated, awaitItem())
        }
    }

    @Test
    fun `given the cache is observed then sections reflects the emitted values`() = runTestWithMainDispatcher {
        // Given
        val cached = listOf(homeSection("s1"))
        val homeRepository = FakeHomeRepository(homeSections = cached)

        // When
        val vm = createViewModel(homeRepository = homeRepository)
        advanceUntilIdle()

        // Then
        assertEquals(cached, vm.sections.value)
    }

    @Test
    fun `given the cache changes when a new value is emitted then sections updates`() = runTestWithMainDispatcher {
        // Given
        val homeRepository = FakeHomeRepository()
        val vm = createViewModel(homeRepository = homeRepository)
        advanceUntilIdle()

        // When
        val updated = listOf(homeSection("s2"))
        homeRepository.emitHomeSections(updated)
        advanceUntilIdle()

        // Then
        assertEquals(updated, vm.sections.value)
    }

    @Test
    fun `given findAll succeeds when created then state is Loaded with the fetched favourite ids`() =
        runTestWithMainDispatcher {
            // Given
            val restaurant = Random.nextRestaurant()
            val guide = Random.nextGuide()
            val section = Random.nextHomeSection(
                isActive = true,
                items = listOf(
                    Random.nextHomeSectionItem(
                        itemType = HomeItemType.RESTAURANT,
                        restaurant = restaurant,
                        guide = null,
                    ),
                    Random.nextHomeSectionItem(itemType = HomeItemType.GUIDE, restaurant = null, guide = guide),
                ),
            )
            val vm = createViewModel(
                homeRepository = FakeHomeRepository(findAllResult = Result.Success(listOf(section))),
                favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(
                    isFavouriteResult = Result.Success(true),
                ),
                favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                    isFavouriteResult = Result.Success(false),
                ),
            )

            // When / Then
            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val loaded = assertIs<HomeUiState.Loaded>(awaitItem())
                assertEquals(setOf(restaurant.id), loaded.favouriteRestaurantIds)
                assertEquals(emptySet<String>(), loaded.favouriteGuideIds)
            }
        }

    @Test
    fun `given findAll fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(
            homeRepository = FakeHomeRepository(
                findAllResult = Result.Failure(DataError.Network(Exception("test error"))),
            ),
        )

        // When / Then
        vm.state.test {
            assertEquals(HomeUiState.Loading, awaitItem())
            assertEquals(HomeUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given a loaded state when refresh is called then isRefreshing toggles back to false`() =
        runTestWithMainDispatcher {
            // Given
            val vm = createViewModel()
            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                assertIs<HomeUiState.Loaded>(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            // When
            vm.refresh()
            advanceUntilIdle()

            // Then
            assertFalse(vm.isRefreshing.value)
            assertIs<HomeUiState.Loaded>(vm.state.value)
        }

    @Test
    fun `given a restaurant not favourite when onToggleRestaurantFavourite is called then marks it optimistically`() =
        runTestWithMainDispatcher {
            // Given
            val restaurant = Random.nextRestaurant()
            val section = Random.nextHomeSection(
                isActive = true,
                items = listOf(
                    Random.nextHomeSectionItem(
                        itemType = HomeItemType.RESTAURANT,
                        restaurant = restaurant,
                        guide = null,
                    ),
                ),
            )
            val favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(
                isFavouriteResult = Result.Success(false),
            )
            val vm = createViewModel(
                homeRepository = FakeHomeRepository(findAllResult = Result.Success(listOf(section))),
                favouriteRestaurantsRepository = favouriteRestaurantsRepository,
            )

            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val initial = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(restaurant.id in initial.favouriteRestaurantIds)

                // When
                vm.onToggleRestaurantFavourite(restaurant)

                // Then
                val toggled = assertIs<HomeUiState.Loaded>(awaitItem())
                assertTrue(restaurant.id in toggled.favouriteRestaurantIds)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, favouriteRestaurantsRepository.markInvokeCount)
            assertEquals(restaurant, favouriteRestaurantsRepository.lastMarkedRestaurant)
        }

    @Test
    fun `given a restaurant favourite when onToggleRestaurantFavourite is called then unmarks it optimistically`() =
        runTestWithMainDispatcher {
            // Given
            val restaurant = Random.nextRestaurant()
            val section = Random.nextHomeSection(
                isActive = true,
                items = listOf(
                    Random.nextHomeSectionItem(
                        itemType = HomeItemType.RESTAURANT,
                        restaurant = restaurant,
                        guide = null,
                    ),
                ),
            )
            val favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(
                isFavouriteResult = Result.Success(true),
            )
            val vm = createViewModel(
                homeRepository = FakeHomeRepository(findAllResult = Result.Success(listOf(section))),
                favouriteRestaurantsRepository = favouriteRestaurantsRepository,
            )

            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val initial = assertIs<HomeUiState.Loaded>(awaitItem())
                assertTrue(restaurant.id in initial.favouriteRestaurantIds)

                // When
                vm.onToggleRestaurantFavourite(restaurant)

                // Then
                val toggled = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(restaurant.id in toggled.favouriteRestaurantIds)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, favouriteRestaurantsRepository.unmarkInvokeCount)
            assertEquals(restaurant.id, favouriteRestaurantsRepository.lastUnmarkedRestaurantId)
        }

    @Test
    fun `given mark fails when onToggleRestaurantFavourite is called then reverts the optimistic flip`() =
        runTestWithMainDispatcher {
            // Given
            val restaurant = Random.nextRestaurant()
            val section = Random.nextHomeSection(
                isActive = true,
                items = listOf(
                    Random.nextHomeSectionItem(
                        itemType = HomeItemType.RESTAURANT,
                        restaurant = restaurant,
                        guide = null,
                    ),
                ),
            )
            val vm = createViewModel(
                homeRepository = FakeHomeRepository(findAllResult = Result.Success(listOf(section))),
                favouriteRestaurantsRepository = FakeFavouriteRestaurantsRepository(
                    isFavouriteResult = Result.Success(false),
                    markResult = Result.Failure(DataError.Network(Exception("test error"))),
                ),
            )

            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val initial = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(restaurant.id in initial.favouriteRestaurantIds)

                // When
                vm.onToggleRestaurantFavourite(restaurant)

                // Then
                val toggled = assertIs<HomeUiState.Loaded>(awaitItem())
                assertTrue(restaurant.id in toggled.favouriteRestaurantIds)

                val reverted = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(restaurant.id in reverted.favouriteRestaurantIds)
            }
        }

    @Test
    fun `given a guide not favourite when onToggleGuideFavourite is called then marks it optimistically`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val section = Random.nextHomeSection(
                isActive = true,
                items = listOf(
                    Random.nextHomeSectionItem(itemType = HomeItemType.GUIDE, restaurant = null, guide = guide),
                ),
            )
            val favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                isFavouriteResult = Result.Success(false),
            )
            val vm = createViewModel(
                homeRepository = FakeHomeRepository(findAllResult = Result.Success(listOf(section))),
                favouritesGuidesRepository = favouritesGuidesRepository,
            )

            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val initial = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(guide.id in initial.favouriteGuideIds)

                // When
                vm.onToggleGuideFavourite(guide)

                // Then
                val toggled = assertIs<HomeUiState.Loaded>(awaitItem())
                assertTrue(guide.id in toggled.favouriteGuideIds)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, favouritesGuidesRepository.markInvokeCount)
            assertEquals(guide, favouritesGuidesRepository.lastMarkedGuide)
        }

    @Test
    fun `given a guide favourite when onToggleGuideFavourite is called then unmarks it optimistically`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val section = Random.nextHomeSection(
                isActive = true,
                items = listOf(
                    Random.nextHomeSectionItem(itemType = HomeItemType.GUIDE, restaurant = null, guide = guide),
                ),
            )
            val favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                isFavouriteResult = Result.Success(true),
            )
            val vm = createViewModel(
                homeRepository = FakeHomeRepository(findAllResult = Result.Success(listOf(section))),
                favouritesGuidesRepository = favouritesGuidesRepository,
            )

            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val initial = assertIs<HomeUiState.Loaded>(awaitItem())
                assertTrue(guide.id in initial.favouriteGuideIds)

                // When
                vm.onToggleGuideFavourite(guide)

                // Then
                val toggled = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(guide.id in toggled.favouriteGuideIds)

                cancelAndIgnoreRemainingEvents()
            }

            advanceUntilIdle()
            assertEquals(1, favouritesGuidesRepository.unmarkInvokeCount)
            assertEquals(guide.id, favouritesGuidesRepository.lastUnmarkedGuideId)
        }

    @Test
    fun `given mark fails when onToggleGuideFavourite is called then reverts the optimistic flip`() =
        runTestWithMainDispatcher {
            // Given
            val guide = Random.nextGuide()
            val section = Random.nextHomeSection(
                isActive = true,
                items = listOf(
                    Random.nextHomeSectionItem(itemType = HomeItemType.GUIDE, restaurant = null, guide = guide),
                ),
            )
            val vm = createViewModel(
                homeRepository = FakeHomeRepository(findAllResult = Result.Success(listOf(section))),
                favouritesGuidesRepository = FakeFavouritesGuidesRepository(
                    isFavouriteResult = Result.Success(false),
                    markResult = Result.Failure(DataError.Network(Exception("test error"))),
                ),
            )

            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val initial = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(guide.id in initial.favouriteGuideIds)

                // When
                vm.onToggleGuideFavourite(guide)

                // Then
                val toggled = assertIs<HomeUiState.Loaded>(awaitItem())
                assertTrue(guide.id in toggled.favouriteGuideIds)

                val reverted = assertIs<HomeUiState.Loaded>(awaitItem())
                assertFalse(guide.id in reverted.favouriteGuideIds)
            }
        }
}
