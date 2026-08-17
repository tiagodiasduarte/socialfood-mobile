package pt.socialfood.presentation.author.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeGetAuthorByIdUseCase
import pt.socialfood.random.nextAuthorDetail
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorDetailViewModelTest {
    @Test
    fun `given getAuthorById succeeds when created then state is Loaded with author`() = runTestWithMainDispatcher {
        // Given
        val author = Random.nextAuthorDetail()
        val vm = AuthorDetailViewModel(
            getAuthorById = FakeGetAuthorByIdUseCase(Result.Success(author)),
            authorId = author.id,
        )

        // When / Then
        vm.state.test {
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertEquals(AuthorDetailUiState.Loaded(author), awaitItem())
        }
    }

    @Test
    fun `given getAuthorById fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetAuthorByIdUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = AuthorDetailViewModel(getAuthorById = useCase, authorId = "author-id")

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
        val useCase = FakeGetAuthorByIdUseCase(Result.Success(author))
        val vm = AuthorDetailViewModel(getAuthorById = useCase, authorId = author.id)

        vm.state.test {
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertIs<AuthorDetailUiState.Loaded>(awaitItem())

            // When
            vm.load()

            // Then
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertIs<AuthorDetailUiState.Loaded>(awaitItem())
        }

        assertEquals(2, useCase.invokeCount)
        assertEquals(author.id, useCase.lastId)
    }
}
