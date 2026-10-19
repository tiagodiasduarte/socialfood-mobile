package pt.socialfood.feature.author

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.testing.fakes.FakeAuthorsRepository
import pt.socialfood.core.testing.fakes.FakeObserveUserUseCase
import pt.socialfood.core.testing.random.nextUser
import pt.socialfood.core.testing.runner.runTestWithMainDispatcher
import pt.socialfood.feature.author.list.AuthorsViewModel
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorsViewModelTest {

    @Test
    fun `given the current user is available when authors is collected then the authors paging flow is requested`() =
        runTestWithMainDispatcher {
            // Given
            val authorsRepository = FakeAuthorsRepository()
            val vm = AuthorsViewModel(authorsRepository, FakeObserveUserUseCase(Random.nextUser()))

            // When
            val job = launch { vm.authors.collect {} }
            advanceUntilIdle()

            // Then
            assertEquals(1, authorsRepository.getAuthorsPagingFlowInvokeCount)
            job.cancel()
        }

    @Test
    fun `given the current user is observed then user reflects the emitted value`() = runTestWithMainDispatcher {
        // Given
        val currentUser = Random.nextUser()
        val observeUser = FakeObserveUserUseCase(initial = currentUser)

        // When / Then
        val vm = AuthorsViewModel(FakeAuthorsRepository(), observeUser)
        vm.user.test {
            awaitItem()
            assertEquals(currentUser, awaitItem())
        }
    }
}
