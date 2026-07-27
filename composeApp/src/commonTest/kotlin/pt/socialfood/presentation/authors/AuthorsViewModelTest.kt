package pt.socialfood.presentation.authors

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeGetAuthorsPagingUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorsViewModelTest {

    @Test
    fun `given authors is collected then getAuthorsPaging is invoked`() = runTestWithMainDispatcher {
        // Given
        val getAuthorsPaging = FakeGetAuthorsPagingUseCase()
        val vm = AuthorsViewModel(getAuthorsPaging)

        // When
        val job = launch { vm.authors.collect {} }
        advanceUntilIdle()

        // Then
        assertEquals(1, getAuthorsPaging.invokeCount)
        job.cancel()
    }
}
