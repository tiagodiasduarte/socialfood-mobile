package pt.socialfood.domain.use_case.favourite.restaurant

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IsRestaurantFavouriteUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the result and forwards the restaurantId`() = runTest {
        // Given
        val restaurantId = Random.nextString()
        val repository = FakeFavouriteRestaurantsRepository(isFavouriteResult = Result.Success(true))
        val useCase = IsRestaurantFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(restaurantId)

        // Then
        assertEquals(Result.Success(true), result)
        assertEquals(restaurantId, repository.lastIsFavouriteRestaurantId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeFavouriteRestaurantsRepository(
            isFavouriteResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = IsRestaurantFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
