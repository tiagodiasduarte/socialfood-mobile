package pt.socialfood.presentation.drawer

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeGetUserMeUseCase
import pt.socialfood.fakes.FakeLogoutUseCase
import pt.socialfood.fakes.FakeObserveUserUseCase
import pt.socialfood.random.nextUser
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DrawerViewModelTest {

    @Test
    fun `given getUserMe succeeds when created then state is Loaded with user`() = runTestWithMainDispatcher {
        // Given
        val user = Random.nextUser()
        val vm = DrawerViewModel(
            getUserMe = FakeGetUserMeUseCase(Result.Success(user)),
            logout = FakeLogoutUseCase(),
            observeUser = FakeObserveUserUseCase(),
        )

        // When / Then
        vm.state.test {
            assertEquals(DrawerUiState.Loading, awaitItem())
            assertEquals(DrawerUiState.Loaded(user), awaitItem())
        }
    }

    @Test
    fun `given getUserMe fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val vm = DrawerViewModel(
            getUserMe = FakeGetUserMeUseCase(Result.Failure(DataError.Network(Exception("test error")))),
            logout = FakeLogoutUseCase(),
            observeUser = FakeObserveUserUseCase(),
        )

        // When / Then
        vm.state.test {
            assertEquals(DrawerUiState.Loading, awaitItem())
            assertEquals(DrawerUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given observeUser emits an updated user when collected then state reflects the new user`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val observeUser = FakeObserveUserUseCase()

            val vm = DrawerViewModel(
                getUserMe = FakeGetUserMeUseCase(Result.Success(user)),
                logout = FakeLogoutUseCase(),
                observeUser = observeUser,
            )
            val updatedUser = user.copy(name = "Updated Name")

            // When / Then
            vm.state.test {
                assertEquals(DrawerUiState.Loading, awaitItem())
                assertEquals(DrawerUiState.Loaded(user), awaitItem())

                observeUser.emit(updatedUser)

                assertEquals(DrawerUiState.Loaded(updatedUser), awaitItem())
            }
        }

    @Test
    fun `given logout is called then invokes logout use case and state becomes LoggedOut`() =
        runTestWithMainDispatcher {
            // Given
            val user = Random.nextUser()
            val logout = FakeLogoutUseCase()
            val vm = DrawerViewModel(
                getUserMe = FakeGetUserMeUseCase(Result.Success(user)),
                logout = logout,
                observeUser = FakeObserveUserUseCase(),
            )

            vm.state.test {
                assertEquals(DrawerUiState.Loading, awaitItem())
                assertEquals(DrawerUiState.Loaded(user), awaitItem())

                // When
                vm.logout()

                // Then
                assertEquals(DrawerUiState.Loading, awaitItem())
                assertEquals(DrawerUiState.LoggedOut, awaitItem())
            }

            advanceUntilIdle()
            assertEquals(1, logout.invokeCount)
        }
}
