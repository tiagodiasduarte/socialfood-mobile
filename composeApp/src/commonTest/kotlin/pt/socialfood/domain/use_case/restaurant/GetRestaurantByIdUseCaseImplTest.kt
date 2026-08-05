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

class GetRestaurantByIdUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the restaurant and forwards the id`() = runTest {
        // Given
        val restaurant = Random.nextRestaurant()
        val repository = FakeRestaurantsRepository(findByIdResult = Result.Success(restaurant))
        val useCase = GetRestaurantByIdUseCaseImpl(repository)

        // When
        val result = useCase(restaurant.id)

        // Then
        assertEquals(Result.Success(restaurant), result)
        assertEquals(restaurant.id, repository.lastFindByIdId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeRestaurantsRepository(
            findByIdResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetRestaurantByIdUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
