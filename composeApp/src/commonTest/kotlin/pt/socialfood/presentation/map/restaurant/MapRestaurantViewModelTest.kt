package pt.socialfood.presentation.map.restaurant

import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.error.ErrorCode
import pt.socialfood.fakes.FakeGetRestaurantByIdUseCase
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextString
import pt.socialfood.runner.runTestWithMainDispatcher
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class MapRestaurantViewModelTest {
    @Test
    fun `given getRestaurantById succeeds when load is called then state is Loaded with the restaurant`() =
        runTestWithMainDispatcher {
            // Given
            val restaurant = Random.nextRestaurant()
            val vm = MapRestaurantViewModel(
                getRestaurantById = FakeGetRestaurantByIdUseCase(Result.Success(restaurant)),
                restaurantId = restaurant.id,
            )

            // When / Then
            vm.state.test {
                assertEquals(MapRestaurantUiState.Loading, awaitItem())
                val loaded = assertIs<MapRestaurantUiState.Loaded>(awaitItem())
                assertEquals(restaurant, loaded.restaurant)
            }
        }

    @Test
    fun `given getRestaurantById fails when load is called then state is Error with the mapped error code`() =
        runTestWithMainDispatcher {
            // Given
            val vm = MapRestaurantViewModel(
                getRestaurantById = FakeGetRestaurantByIdUseCase(
                    Result.Failure(DataError.Network(Exception("test error"))),
                ),
                restaurantId = Random.nextString(),
            )

            // When / Then
            vm.state.test {
                assertEquals(MapRestaurantUiState.Loading, awaitItem())
                assertEquals(MapRestaurantUiState.Error(ErrorCode.NETWORK), awaitItem())
            }
        }
}
