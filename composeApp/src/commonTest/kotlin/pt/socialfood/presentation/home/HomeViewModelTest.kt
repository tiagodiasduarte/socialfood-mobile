package pt.socialfood.presentation.home

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
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
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

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
    fun `given getHomeSections fails when created then state is Error with the backend message`() =
        runTestWithMainDispatcher {
            // Given
            val getHomeSections = FakeGetHomeSectionsUseCase(Result.Failure(DataError.Network(Exception("test error"))))

            // When / Then
            val vm = createViewModel(getHomeSections = getHomeSections)
            vm.state.test {
                assertEquals(HomeUiState.Loading, awaitItem())
                val error = assertIs<HomeUiState.Error>(awaitItem())
                assertEquals("Something went wrong", error.message)
            }
        }

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
}
