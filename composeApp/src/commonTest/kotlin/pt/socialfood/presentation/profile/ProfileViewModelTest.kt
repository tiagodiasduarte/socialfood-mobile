package pt.socialfood.presentation.profile

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeGetAuthorByIdUseCase
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.random.nextAuthorDetail
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @Test
    fun `given getUserMe and getAuthorById succeed when created then state is Loaded with author`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val author = Random.nextAuthorDetail(id = user.id)
            val vm = ProfileViewModel(
                getUserMe = FakeGetUserMeUseCase(Result.Success(user)),
                getAuthorById = FakeGetAuthorByIdUseCase(Result.Success(author)),
            )

            // When / Then
            vm.state.test {
                assertEquals(ProfileUiState.Loading, awaitItem())
                assertEquals(ProfileUiState.Loaded(author), awaitItem())
            }
        }

    @Test
    fun `given getUserMe fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val getUserMe = FakeGetUserMeUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = ProfileViewModel(
            getUserMe = getUserMe,
            getAuthorById = FakeGetAuthorByIdUseCase(Result.Success(Random.nextAuthorDetail())),
        )

        // When / Then
        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertEquals(ProfileUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given getAuthorById fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val user = Random.nextUser()
        val getAuthorById = FakeGetAuthorByIdUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = ProfileViewModel(
            getUserMe = FakeGetUserMeUseCase(Result.Success(user)),
            getAuthorById = getAuthorById,
        )

        // When / Then
        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertEquals(ProfileUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given getUserMe succeeds when created then getAuthorById is called with the current user id`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val getAuthorById = FakeGetAuthorByIdUseCase(Result.Success(Random.nextAuthorDetail(id = user.id)))
            val vm = ProfileViewModel(
                getUserMe = FakeGetUserMeUseCase(Result.Success(user)),
                getAuthorById = getAuthorById,
            )

            // When / Then
            vm.state.test {
                assertEquals(ProfileUiState.Loading, awaitItem())
                assertIs<ProfileUiState.Loaded>(awaitItem())
            }

            assertEquals(user.id, getAuthorById.lastId)
        }

    @Test
    fun `given a loaded profile when load is called then reloads it`() = runTestWithMainDispatcher {
        // Given
        val user = Random.nextUser()
        val getUserMe = FakeGetUserMeUseCase(Result.Success(user))
        val getAuthorById = FakeGetAuthorByIdUseCase(Result.Success(Random.nextAuthorDetail(id = user.id)))
        val vm = ProfileViewModel(getUserMe = getUserMe, getAuthorById = getAuthorById)

        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertIs<ProfileUiState.Loaded>(awaitItem())

            // When
            vm.load()

            // Then
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertIs<ProfileUiState.Loaded>(awaitItem())
        }

        assertEquals(2, getUserMe.invokeCount)
        assertEquals(2, getAuthorById.invokeCount)
    }
}
