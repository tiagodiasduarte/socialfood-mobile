package pt.socialfood.presentation.author.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.fakes.FakeGetAuthorByIdUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorDetailViewModelTest {
    private val fakeAuthorDetail =
        AuthorDetail(
            id = "author-id",
            name = "Author Name",
            username = "authorname",
        )

    @Test
    fun `given getAuthorById succeeds when created then state is Loaded with author`() = runTestWithMainDispatcher {
        // Given
        val vm =
            AuthorDetailViewModel(
                getAuthorById = FakeGetAuthorByIdUseCase(Result.Success(fakeAuthorDetail)),
                authorId = fakeAuthorDetail.id,
            )

        // When / Then
        vm.state.test {
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            assertEquals(AuthorDetailUiState.Loaded(fakeAuthorDetail), awaitItem())
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
        val useCase = FakeGetAuthorByIdUseCase(Result.Success(fakeAuthorDetail))
        val vm = AuthorDetailViewModel(getAuthorById = useCase, authorId = fakeAuthorDetail.id)

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
        assertEquals(fakeAuthorDetail.id, useCase.lastId)
    }
}
