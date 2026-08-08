package pt.socialfood.presentation.search

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeSearchUseCase
import pt.socialfood.random.nextSearch
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @Test
    fun `given a blank query when onSearchQueryChange is called then state is Loaded with empty results`() =
        runTestWithMainDispatcher {
            // Given
            val vm = SearchViewModel(FakeSearchUseCase())

            // When
            vm.onSearchQueryChange("   ")

            // Then
            assertEquals(SearchUiState.Loaded(emptyList()), vm.state.value)
            assertEquals("   ", vm.searchQuery)
        }

    @Test
    fun `given a query shorter than the minimum length when onSearchQueryChange is called then search is skipped`() =
        runTestWithMainDispatcher {
            // Given
            val search = FakeSearchUseCase()
            val vm = SearchViewModel(search)

            // When
            vm.onSearchQueryChange("a")
            advanceUntilIdle()

            // Then
            assertEquals(0, search.invokeCount)
            assertEquals("a", vm.searchQuery)
        }

    @Test
    fun `given search succeeds when onSearchQueryChange is called then state is Loaded with the results`() =
        runTestWithMainDispatcher {
            // Given
            val results = listOf(Random.nextSearch())
            val search = FakeSearchUseCase(Result.Success(results))
            val vm = SearchViewModel(search)

            // When / Then
            vm.state.test {
                assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

                vm.onSearchQueryChange("pizza")

                assertEquals(SearchUiState.Loading, awaitItem())
                assertEquals(SearchUiState.Loaded(results), awaitItem())
            }
            assertEquals(1, search.invokeCount)
        }

    @Test
    fun `given search fails when onSearchQueryChange is called then state is Error`() = runTestWithMainDispatcher {
        // Given
        val search = FakeSearchUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = SearchViewModel(search)

        // When / Then
        vm.state.test {
            assertEquals(SearchUiState.Loaded(emptyList()), awaitItem())

            vm.onSearchQueryChange("pizza")

            assertEquals(SearchUiState.Loading, awaitItem())
            assertEquals(SearchUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given a rapid query change when onSearchQueryChange is called then only the last query is searched`() =
        runTestWithMainDispatcher {
            // Given
            val results = listOf(Random.nextSearch())
            val search = FakeSearchUseCase(Result.Success(results))
            val vm = SearchViewModel(search)

            // When
            vm.onSearchQueryChange("pi")
            vm.onSearchQueryChange("pizza")
            advanceUntilIdle()

            // Then
            assertEquals(1, search.invokeCount)
            assertEquals("pizza", vm.searchQuery)
        }
}
