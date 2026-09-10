package pt.socialfood.presentation.profile

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeGetUserAuthorProfileUseCase
import pt.socialfood.random.nextAuthorDetail
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @Test
    fun `given getUserAuthorProfile succeeds when created then state is Loaded with author`() =
        runTestWithMainDispatcher {
            // Given
            val author = Random.nextAuthorDetail()
            val vm = ProfileViewModel(getUserAuthorProfile = FakeGetUserAuthorProfileUseCase(Result.Success(author)))

            // When / Then
            vm.state.test {
                assertEquals(ProfileUiState.Loading, awaitItem())
                assertEquals(ProfileUiState.Loaded(author), awaitItem())
            }
        }

    @Test
    fun `given getUserAuthorProfile fails when created then state is Error`() = runTestWithMainDispatcher {
        // Given
        val useCase = FakeGetUserAuthorProfileUseCase(Result.Failure(DataError.Network(Exception("test error"))))
        val vm = ProfileViewModel(getUserAuthorProfile = useCase)

        // When / Then
        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertEquals(ProfileUiState.Error(ErrorCode.NETWORK), awaitItem())
        }
    }

    @Test
    fun `given a loaded profile when load is called then reloads it`() = runTestWithMainDispatcher {
        // Given
        val author = Random.nextAuthorDetail()
        val useCase = FakeGetUserAuthorProfileUseCase(Result.Success(author))
        val vm = ProfileViewModel(getUserAuthorProfile = useCase)

        vm.state.test {
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertIs<ProfileUiState.Loaded>(awaitItem())

            // When
            vm.load()

            // Then
            assertEquals(ProfileUiState.Loading, awaitItem())
            assertIs<ProfileUiState.Loaded>(awaitItem())
        }

        assertEquals(2, useCase.invokeCount)
    }
}
