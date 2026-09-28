package pt.socialfood.presentation.guide.map

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeGetGuideByIdUseCase
import pt.socialfood.random.nextGuide
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextString
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class GuideMapViewModelTest {
    @Test
    fun `given getGuideById succeeds when load is called then state is Loaded with the guide`() =
        runTestWithMainDispatcher {
            // Given
            val restaurants = listOf(Random.nextRestaurant(), Random.nextRestaurant())
            val guide = Random.nextGuide(restaurants = restaurants)
            val vm = GuideMapViewModel(
                getGuideById = FakeGetGuideByIdUseCase(Result.Success(guide)),
                guideId = guide.id,
            )

            // When / Then
            vm.state.test {
                assertEquals(GuideMapUiState.Loading, awaitItem())
                val loaded = assertIs<GuideMapUiState.Loaded>(awaitItem())
                assertEquals(guide, loaded.guide)
            }
        }

    @Test
    fun `given getGuideById fails when load is called then state is Error with the mapped error code`() =
        runTestWithMainDispatcher {
            // Given
            val vm = GuideMapViewModel(
                getGuideById = FakeGetGuideByIdUseCase(Result.Failure(DataError.Network(Exception("test error")))),
                guideId = Random.nextString(),
            )

            // When / Then
            vm.state.test {
                assertEquals(GuideMapUiState.Loading, awaitItem())
                assertEquals(GuideMapUiState.Error(ErrorCode.NETWORK), awaitItem())
            }
        }
}
