package pt.socialfood.presentation.profile

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.User
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeLogoutUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    private val fakeUser = User(id = "user-1", email = "user@test.com", name = "Jane Doe", username = "janedoe")

    @Test
    fun `given loading the user fails when created then state is Error with the backend message`() =
        runTestWithMainDispatcher {
            // Given
            val getUserMe = FakeGetUserMeUseCase(Result.Failure(DataError.Network(Exception("test error"))))

            // When / Then
            val vm = ProfileViewModel(getUserMe, FakeLogoutUseCase(), FakeObserveUserUseCase())
            vm.state.test {
                assertEquals(ProfileUiState.Loading, awaitItem())
                val error = assertIs<ProfileUiState.Error>(awaitItem())
                assertEquals("Something went wrong", error.message)
            }
        }

    @Test
    fun `given loading the user succeeds when created then state is Loaded`() = runTestWithMainDispatcher {
        // Given
        val getUserMe = FakeGetUserMeUseCase(Result.Success(fakeUser))

        // When / Then
        val vm = ProfileViewModel(getUserMe, FakeLogoutUseCase(), FakeObserveUserUseCase())
        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            val loaded = assertIs<ProfileUiState.Loaded>(awaitItem())
            assertEquals(fakeUser, loaded.user)
        }
    }

    @Test
    fun `given logout is called then use case is invoked and state becomes LoggedOut`() = runTestWithMainDispatcher {
        // Given
        val logout = FakeLogoutUseCase()
        val vm = ProfileViewModel(FakeGetUserMeUseCase(Result.Success(fakeUser)), logout, FakeObserveUserUseCase())

        // When / Then
        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertIs<ProfileUiState.Loaded>(awaitItem())

            vm.logout()

            assertEquals(ProfileUiState.Loading, awaitItem())
            assertEquals(ProfileUiState.LoggedOut, awaitItem())
            assertEquals(1, logout.invokeCount)
        }
    }
}
