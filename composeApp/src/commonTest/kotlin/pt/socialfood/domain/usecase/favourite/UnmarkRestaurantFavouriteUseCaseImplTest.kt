package pt.socialfood.domain.usecase.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.usecase.favourite.restaurant.UnmarkRestaurantFavouriteUseCaseImpl
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UnmarkRestaurantFavouriteUseCaseImplTest {

    @Test
    fun `given repository succeeds when invoked then delegates restaurantId and returns Success`() = runTest {
        // Given
        val repository = FakeFavouriteRestaurantsRepository(unmarkResult = Result.Success(Unit))
        val useCase = UnmarkRestaurantFavouriteUseCaseImpl(repository)

        // When
        val result = useCase("restaurant-id")

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("restaurant-id", repository.lastUnmarkedRestaurantId)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeFavouriteRestaurantsRepository(
                unmarkResult = Result.Failure(DataError.Network(Exception("test error"))),
            )
        val useCase = UnmarkRestaurantFavouriteUseCaseImpl(repository)

        // When
        val result = useCase("restaurant-id")

        // Then
        assertIs<Result.Failure>(result)
    }
}
