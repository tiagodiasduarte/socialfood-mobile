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
import pt.socialfood.domain.use_case.favourite.guide.IsGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.IsRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.MarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.UnmarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.home.GetHomeSectionsUseCase
import pt.socialfood.domain.use_case.home.ObserveHomeSectionsUseCase
import pt.socialfood.fakes.FakeGetHomeSectionsUseCase
import pt.socialfood.fakes.FakeIsGuideFavouriteUseCase
import pt.socialfood.fakes.FakeIsRestaurantFavouriteUseCase
import pt.socialfood.fakes.FakeMarkGuideFavouriteUseCase
import pt.socialfood.fakes.FakeMarkRestaurantFavouriteUseCase
import pt.socialfood.fakes.FakeObserveHomeSectionsUseCase
import pt.socialfood.fakes.FakeUnmarkGuideFavouriteUseCase
import pt.socialfood.fakes.FakeUnmarkRestaurantFavouriteUseCase
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextHomeSection
import pt.socialfood.random.nextHomeSectionItem
import pt.socialfood.random.nextRestaurant
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
        getHomeSections: GetHomeSectionsUseCase = FakeGetHomeSectionsUseCase(),
        isRestaurantFavourite: IsRestaurantFavouriteUseCase = FakeIsRestaurantFavouriteUseCase(),
        markRestaurantFavourite: MarkRestaurantFavouriteUseCase = FakeMarkRestaurantFavouriteUseCase(),
        unmarkRestaurantFavourite: UnmarkRestaurantFavouriteUseCase = FakeUnmarkRestaurantFavouriteUseCase(),
        isGuideFavourite: IsGuideFavouriteUseCase = FakeIsGuideFavouriteUseCase(),
        markGuideFavourite: MarkGuideFavouriteUseCase = FakeMarkGuideFavouriteUseCase(),
        unmarkGuideFavourite: UnmarkGuideFavouriteUseCase = FakeUnmarkGuideFavouriteUseCase(),
        observeHomeSections: ObserveHomeSectionsUseCase = FakeObserveHomeSectionsUseCase(),
    ) = HomeViewModel(
        getHomeSections,
        isRestaurantFavourite,
        markRestaurantFavourite,
        unmarkRestaurantFavourite,
        isGuideFavourite,
        markGuideFavourite,
        unmarkGuideFavourite,
        observeHomeSections,
    )

    @Test
    fun `given the cache is observed then sections reflects the emitted values`() = runTestWithMainDispatcher {
        // Given
        val cached = listOf(homeSection("s1"))
        val observeHomeSections = FakeObserveHomeSectionsUseCase(initial = cached)

        // When
        val vm = createViewModel(observeHomeSections = observeHomeSections)
        advanceUntilIdle()

        // Then
        assertEquals(cached, vm.sections.value)
    }

    @Test
    fun `given the cache changes when a new value is emitted then sections updates`() = runTestWithMainDispatcher {
        // Given
        val observeHomeSections = FakeObserveHomeSectionsUseCase()
        val vm = createViewModel(observeHomeSections = observeHomeSections)
        advanceUntilIdle()

        // When
        val updated = listOf(homeSection("s2"))
        observeHomeSections.emit(updated)
        advanceUntilIdle()

        // Then
        assertEquals(updated, vm.sections.value)
    }

    @Test
    fun `given getHomeSections succeeds when created then state is Loaded with the fetched favourite ids`() =
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
                getHomeSections = FakeGetHomeSectionsUseCase(Result.Success(listOf(section))),
                isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(true)),
                isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(false)),
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
    fun `given getHomeSections fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val vm = createViewModel(
            getHomeSections = FakeGetHomeSectionsUseCase(Result.Failure(DataError.Network(Exception("test error")))),
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
            val markRestaurantFavourite = FakeMarkRestaurantFavouriteUseCase()
            val vm = createViewModel(
                getHomeSections = FakeGetHomeSectionsUseCase(Result.Success(listOf(section))),
                isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(false)),
                markRestaurantFavourite = markRestaurantFavourite,
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
            assertEquals(1, markRestaurantFavourite.invokeCount)
            assertEquals(restaurant, markRestaurantFavourite.lastRestaurant)
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
            val unmarkRestaurantFavourite = FakeUnmarkRestaurantFavouriteUseCase()
            val vm = createViewModel(
                getHomeSections = FakeGetHomeSectionsUseCase(Result.Success(listOf(section))),
                isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(true)),
                unmarkRestaurantFavourite = unmarkRestaurantFavourite,
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
            assertEquals(1, unmarkRestaurantFavourite.invokeCount)
            assertEquals(restaurant.id, unmarkRestaurantFavourite.lastRestaurantId)
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
                getHomeSections = FakeGetHomeSectionsUseCase(Result.Success(listOf(section))),
                isRestaurantFavourite = FakeIsRestaurantFavouriteUseCase(Result.Success(false)),
                markRestaurantFavourite = FakeMarkRestaurantFavouriteUseCase(
                    Result.Failure(DataError.Network(Exception("test error"))),
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
            val markGuideFavourite = FakeMarkGuideFavouriteUseCase()
            val vm = createViewModel(
                getHomeSections = FakeGetHomeSectionsUseCase(Result.Success(listOf(section))),
                isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(false)),
                markGuideFavourite = markGuideFavourite,
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
            assertEquals(1, markGuideFavourite.invokeCount)
            assertEquals(guide, markGuideFavourite.lastGuide)
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
            val unmarkGuideFavourite = FakeUnmarkGuideFavouriteUseCase()
            val vm = createViewModel(
                getHomeSections = FakeGetHomeSectionsUseCase(Result.Success(listOf(section))),
                isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(true)),
                unmarkGuideFavourite = unmarkGuideFavourite,
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
            assertEquals(1, unmarkGuideFavourite.invokeCount)
            assertEquals(guide.id, unmarkGuideFavourite.lastGuideId)
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
                getHomeSections = FakeGetHomeSectionsUseCase(Result.Success(listOf(section))),
                isGuideFavourite = FakeIsGuideFavouriteUseCase(Result.Success(false)),
                markGuideFavourite = FakeMarkGuideFavouriteUseCase(
                    Result.Failure(DataError.Network(Exception("test error"))),
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
