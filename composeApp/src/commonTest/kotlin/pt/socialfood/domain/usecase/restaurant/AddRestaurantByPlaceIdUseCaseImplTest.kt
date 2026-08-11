package pt.socialfood.domain.usecase.restaurant

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeRestaurantsRepository
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class AddRestaurantByPlaceIdUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success and forwards the placeId`() = runTest {
        // Given
        val placeId = Random.nextString()
        val repository = FakeRestaurantsRepository(addByPlaceIdResult = Result.Success(Unit))
        val useCase = AddRestaurantByPlaceIdUseCaseImpl(repository)

        // When
        val result = useCase(placeId)

        // Then
        assertEquals(Result.Success(Unit), result)
        assertEquals(placeId, repository.lastAddByPlaceIdPlaceId)
        assertEquals(1, repository.addByPlaceIdInvokeCount)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeRestaurantsRepository(
            addByPlaceIdResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = AddRestaurantByPlaceIdUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
