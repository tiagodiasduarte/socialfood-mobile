package pt.socialfood.presentation.author.detail

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.AuthorDetail
import pt.socialfood.fakes.FakeGetAuthorByIdUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class AuthorDetailViewModelTest {
    private val fakeAuthor = AuthorDetail(id = "author-id", name = "Author Name", username = "author")

    @Test
    fun `given loading the author fails when created then state is Error with the backend message`() =
        runTestWithMainDispatcher {
            // Given
            val getAuthorById = FakeGetAuthorByIdUseCase(Result.Failure(DataError.Network(Exception("test error"))))

            // When / Then
            val vm = AuthorDetailViewModel(getAuthorById, authorId = "author-id")
            vm.state.test {
                assertEquals(AuthorDetailUiState.Loading, awaitItem())
                val error = assertIs<AuthorDetailUiState.Error>(awaitItem())
                assertEquals("Something went wrong", error.message)
            }
        }

    @Test
    fun `given loading the author succeeds when created then state is Loaded`() = runTestWithMainDispatcher {
        // Given
        val getAuthorById = FakeGetAuthorByIdUseCase(Result.Success(fakeAuthor))

        // When / Then
        val vm = AuthorDetailViewModel(getAuthorById, authorId = fakeAuthor.id)
        vm.state.test {
            assertEquals(AuthorDetailUiState.Loading, awaitItem())
            val loaded = assertIs<AuthorDetailUiState.Loaded>(awaitItem())
            assertEquals(fakeAuthor, loaded.author)
        }
    }
}
