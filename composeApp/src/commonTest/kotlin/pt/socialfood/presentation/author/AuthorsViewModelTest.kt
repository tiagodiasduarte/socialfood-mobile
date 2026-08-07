package pt.socialfood.presentation.author

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.fakes.FakeGetAuthorsPagingUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.presentation.author.list.AuthorsViewModel
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorsViewModelTest {

    @Test
    fun `given authors is collected then getAuthorsPaging is invoked`() = runTestWithMainDispatcher {
        // Given
        val getAuthorsPaging = FakeGetAuthorsPagingUseCase()
        val vm = AuthorsViewModel(getAuthorsPaging, FakeObserveUserUseCase())

        // When
        val job = launch { vm.authors.collect {} }
        advanceUntilIdle()

        // Then
        assertEquals(1, getAuthorsPaging.invokeCount)
        job.cancel()
    }

    @Test
    fun `given the current user is observed then user reflects the emitted value`() = runTestWithMainDispatcher {
        // Given
        val currentUser = Random.nextUser()
        val observeUser = FakeObserveUserUseCase(initial = currentUser)

        // When / Then
        val vm = AuthorsViewModel(FakeGetAuthorsPagingUseCase(), observeUser)
        vm.user.test {
            awaitItem()
            assertEquals(currentUser, awaitItem())
        }
    }
}
