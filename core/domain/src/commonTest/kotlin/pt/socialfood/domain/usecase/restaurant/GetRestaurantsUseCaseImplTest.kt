package pt.socialfood.domain.usecase.restaurant

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeRestaurantsRepository
import pt.socialfood.random.nextRestaurant
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class GetRestaurantsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns its restaurants`() = runTest {
        // Given
        val restaurants = listOf(Random.nextRestaurant(), Random.nextRestaurant())
        val repository = FakeRestaurantsRepository(findAllResult = Result.Success(restaurants))
        val useCase = GetRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.Success(restaurants), result)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeRestaurantsRepository(
            findAllResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
