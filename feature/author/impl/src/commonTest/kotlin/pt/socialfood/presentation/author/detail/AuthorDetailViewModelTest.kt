package pt.socialfood.presentation.author.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeAuthorsRepository
import pt.socialfood.random.nextAuthorDetail
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorDetailViewModelTest {
    @Test
    fun `given findAuthorById succeeds when created then state is Loaded with author`() = runTestWithMainDispatcher {
        // Given
        val author = Random.nextAuthorDetail()
        val vm = AuthorDetailViewModel(
            authorsRepository = FakeAuthorsRepository(findAuthorByIdResult = Result.Success(author)),
            authorId = author.id,
        )

        // When / Then
        vm.state.test {
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertEquals(AuthorDetailUiState.Loaded(author), awaitItem())
        }
    }

    @Test
    fun `given findAuthorById fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val authorsRepository = FakeAuthorsRepository(
            findAuthorByIdResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val vm = AuthorDetailViewModel(authorsRepository = authorsRepository, authorId = "author-id")

        // When / Then
        vm.state.test {
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertEquals(AuthorDetailUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given a loaded author when load is called then reloads it`() = runTestWithMainDispatcher {
        // Given
        val author = Random.nextAuthorDetail()
        val authorsRepository = FakeAuthorsRepository(findAuthorByIdResult = Result.Success(author))
        val vm = AuthorDetailViewModel(authorsRepository = authorsRepository, authorId = author.id)

        vm.state.test {
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertIs<AuthorDetailUiState.Loaded>(awaitItem())

            // When
            vm.load()

            // Then
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertIs<AuthorDetailUiState.Loaded>(awaitItem())
        }

        assertEquals(2, authorsRepository.findAuthorByIdInvokeCount)
        assertEquals(author.id, authorsRepository.lastFindAuthorByIdId)
    }
}
