package pt.socialfood.domain.use_case.restaurant

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeRestaurantsRepository
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AwaitEnrichedRestaurantByPlaceIdUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the restaurant and forwards the placeId`() = runTest {
        // Given
        val restaurant = Random.nextRestaurant()
        val placeId = Random.nextString()
        val repository = FakeRestaurantsRepository(awaitEnrichedResult = Result.Success(restaurant))
        val useCase = AwaitEnrichedRestaurantByPlaceIdUseCaseImpl(repository)

        // When
        val result = useCase(placeId)

        // Then
        assertEquals(Result.Success(restaurant), result)
        assertEquals(placeId, repository.lastAwaitEnrichedPlaceId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeRestaurantsRepository(
            awaitEnrichedResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = AwaitEnrichedRestaurantByPlaceIdUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
