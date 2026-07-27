package pt.socialfood.presentation.authors

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ErrorEntity
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.PagedAuthors
import pt.socialfood.fakes.FakeFindAuthorsUseCase
import pt.socialfood.fakes.FakeGetAuthorsPagingUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorsViewModelTest {

    private fun author(id: String) = Author(id = id, name = "Author $id")

    @Test
    fun `given blank search query when created then state stays Loading and no search fetch happens`() = runTestWithMainDispatcher {
        // Given
        val findAuthors = FakeFindAuthorsUseCase { page, _ ->
            Result.Success(PagedAuthors(authors = listOf(author("a1")), page = page, hasMore = false))
        }

        // When
        val vm = AuthorsViewModel(findAuthors, FakeGetAuthorsPagingUseCase())
        advanceUntilIdle()

        // Then
        assertIs<AuthorsUiState.Loading>(vm.state.value)
        assertEquals(null, findAuthors.lastQuery)
    }

    @Test
    fun `given search use case fails when query becomes non-blank then state is Error`() = runTestWithMainDispatcher {
        // Given
        val findAuthors = FakeFindAuthorsUseCase { _, _ -> Result.Error(ErrorEntity.Unknown) }
        val vm = AuthorsViewModel(findAuthors, FakeGetAuthorsPagingUseCase())
        advanceUntilIdle()

        // When
        vm.onSearchQueryChange("john")
        advanceUntilIdle()

        // Then
        assertIs<AuthorsUiState.Error>(vm.state.value)
    }

    @Test
    fun `given search query changes when debounce elapses then reloads filtered by query`() = runTestWithMainDispatcher {
        // Given
        val findAuthors = FakeFindAuthorsUseCase()
        val vm = AuthorsViewModel(findAuthors, FakeGetAuthorsPagingUseCase())
        advanceUntilIdle()

        // When
        vm.onSearchQueryChange("john")
        advanceUntilIdle()

        // Then
        assertEquals("john", findAuthors.lastQuery)
        assertIs<AuthorsUiState.Loaded>(vm.state.value)
    }

    @Test
    fun `given an active search with more pages when loadMore is called then appends authors and updates hasMore`() = runTestWithMainDispatcher {
        // Given
        val findAuthors = FakeFindAuthorsUseCase { page, _ ->
            if (page == 1) {
                Result.Success(PagedAuthors(authors = listOf(author("a1")), page = 1, hasMore = true))
            } else {
                Result.Success(PagedAuthors(authors = listOf(author("a2")), page = 2, hasMore = false))
            }
        }
        val vm = AuthorsViewModel(findAuthors, FakeGetAuthorsPagingUseCase())
        vm.onSearchQueryChange("john")
        advanceUntilIdle()

        // When
        vm.loadMore()
        advanceUntilIdle()

        // Then
        val state = assertIs<AuthorsUiState.Loaded>(vm.state.value)
        assertEquals(listOf("a1", "a2"), state.authors.map { it.id })
        assertTrue(!state.hasMore)
    }

    @Test
    fun `given an active search when refresh is called then reloads first page and clears isRefreshing`() = runTestWithMainDispatcher {
        // Given
        val findAuthors = FakeFindAuthorsUseCase { page, _ ->
            Result.Success(PagedAuthors(authors = listOf(author("a1")), page = page, hasMore = false))
        }
        val vm = AuthorsViewModel(findAuthors, FakeGetAuthorsPagingUseCase())
        vm.onSearchQueryChange("john")
        advanceUntilIdle()

        // When
        vm.refresh()
        advanceUntilIdle()

        // Then
        assertEquals(false, vm.isRefreshing.value)
        assertIs<AuthorsUiState.Loaded>(vm.state.value)
    }

    @Test
    fun `given authors is collected then getAuthorsPaging is invoked`() = runTestWithMainDispatcher {
        // Given
        val getAuthorsPaging = FakeGetAuthorsPagingUseCase()
        val vm = AuthorsViewModel(FakeFindAuthorsUseCase(), getAuthorsPaging)

        // When
        val job = launch { vm.authors.collect {} }
        advanceUntilIdle()

        // Then
        assertEquals(1, getAuthorsPaging.invokeCount)
        job.cancel()
    }
}
