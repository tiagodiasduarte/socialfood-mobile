package pt.socialfood.presentation.favourite

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.FavouriteGuide
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.fakes.FakeGetFavouriteGuidesUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteGuidesViewModelTest {

    private fun favourite(id: String) = FavouriteGuide(
        guide = Guide(
            id = id,
            name = "Guide $id",
            description = "",
            visibility = GuideVisibility.PUBLIC,
            author = Author(id = "author-id", name = "Author"),
            numberOfRestaurant = 0,
        ),
        favouritedAt = 0L,
    )

    @Test
    fun `given favourites exist when created then loads first page into Loaded state`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetFavouriteGuidesUseCase {
            Result.Success(PagedFavouriteGuides(favourites = listOf(favourite("g1")), page = it, total = 1, hasMore = false))
        }

        // When
        val vm = FavouriteGuidesViewModel(useCase)
        advanceUntilIdle()

        // Then
        val state = assertIs<FavouriteGuidesUiState.Loaded>(vm.state.value)
        assertEquals(1, state.guides.size)
        assertEquals("g1", state.guides.first().id)
    }

    @Test
    fun `given use case fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetFavouriteGuidesUseCase { Result.Error(ErrorEntity.Unknown) }

        // When
        val vm = FavouriteGuidesViewModel(useCase)
        advanceUntilIdle()

        // Then
        assertIs<FavouriteGuidesUiState.Error>(vm.state.value)
    }

    @Test
    fun `given more pages available when loadMore is called then appends guides and updates hasMore`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetFavouriteGuidesUseCase { page ->
            if (page == 1) {
                Result.Success(PagedFavouriteGuides(favourites = listOf(favourite("g1")), page = 1, total = 2, hasMore = true))
            } else {
                Result.Success(PagedFavouriteGuides(favourites = listOf(favourite("g2")), page = 2, total = 2, hasMore = false))
            }
        }
        val vm = FavouriteGuidesViewModel(useCase)
        advanceUntilIdle()

        // When
        vm.loadMore()
        advanceUntilIdle()

        // Then
        val state = assertIs<FavouriteGuidesUiState.Loaded>(vm.state.value)
        assertEquals(listOf("g1", "g2"), state.guides.map { it.id })
        assertTrue(!state.hasMore)
    }

    @Test
    fun `given refresh is called then reloads first page and clears isRefreshing`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetFavouriteGuidesUseCase {
            Result.Success(PagedFavouriteGuides(favourites = listOf(favourite("g1")), page = it, total = 1, hasMore = false))
        }
        val vm = FavouriteGuidesViewModel(useCase)
        advanceUntilIdle()

        // When
        vm.refresh()
        advanceUntilIdle()

        // Then
        assertEquals(false, vm.isRefreshing.value)
        assertIs<FavouriteGuidesUiState.Loaded>(vm.state.value)
    }
}
