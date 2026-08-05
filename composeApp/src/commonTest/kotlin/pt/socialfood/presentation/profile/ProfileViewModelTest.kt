package pt.socialfood.presentation.profile

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeLogoutUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val fakeUser = User(id = "user-id", email = "user@test.com", name = "Test User", username = "testuser")

    @Test
    fun `given getUserMe succeeds when created then state is Loaded with user`() = runTestWithMainDispatcher {
        // Given
        val vm =
            ProfileViewModel(
                getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser)),
                logout = FakeLogoutUseCase(),
                observeUser = FakeObserveUserUseCase(),
            )

        // When / Then
        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertEquals(ProfileUiState.Loaded(fakeUser), awaitItem())
        }
    }

    @Test
    fun `given getUserMe fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val vm =
            ProfileViewModel(
                getUserMe = FakeGetUserMeUseCase(Result.Failure(DataError.Network(Exception("test error")))),
                logout = FakeLogoutUseCase(),
                observeUser = FakeObserveUserUseCase(),
            )

        // When / Then
        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertEquals(ProfileUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given observeUser emits an updated user when collected then state reflects the new user`() =
        runTestWithMainDispatcher {
            // Given
            val observeUser = FakeObserveUserUseCase()
            val vm =
                ProfileViewModel(
                    getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser)),
                    logout = FakeLogoutUseCase(),
                    observeUser = observeUser,
                )
            val updatedUser = fakeUser.copy(name = "Updated Name")

            // When / Then
            vm.state.test {
                assertEquals(ProfileUiState.Loading, awaitItem())
                assertEquals(ProfileUiState.Loaded(fakeUser), awaitItem())

                observeUser.emit(updatedUser)

                assertEquals(ProfileUiState.Loaded(updatedUser), awaitItem())
            }
        }

    @Test
    fun `given logout is called then invokes logout use case and state becomes LoggedOut`() =
        runTestWithMainDispatcher {
            // Given
            val logout = FakeLogoutUseCase()
            val vm =
                ProfileViewModel(
                    getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser)),
                    logout = logout,
                    observeUser = FakeObserveUserUseCase(),
                )

            vm.state.test {
                assertEquals(ProfileUiState.Loading, awaitItem())
                assertEquals(ProfileUiState.Loaded(fakeUser), awaitItem())

                // When
                vm.logout()

                // Then
                assertEquals(ProfileUiState.Loading, awaitItem())
                assertEquals(ProfileUiState.LoggedOut, awaitItem())
            }

            advanceUntilIdle()
            assertEquals(1, logout.invokeCount)
        }
}
