package pt.socialfood.domain.use_case.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.PagedFavouriteRestaurants
import pt.socialfood.domain.use_case.favourite.restaurant.GetFavouriteRestaurantsUseCaseImpl
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import kotlin.test.Test
import kotlin.test.assertIs

class GetFavouriteRestaurantsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success with PagedFavouriteRestaurants`() =
        runTest {
            // Given
            val repository =
                FakeFavouriteRestaurantsRepository(
                    pagedResult =
                        Result.Success(
                            PagedFavouriteRestaurants(favourites = emptyList(), page = 1, total = 0, hasMore = false),
                        ),
                )
            val useCase = GetFavouriteRestaurantsUseCaseImpl(repository)

            // When
            val result = useCase(page = 1, limit = 10)

            // Then
            assertIs<Result.Success<PagedFavouriteRestaurants>>(result)
        }

    @Test
    fun `given repository fails when invoked then returns Error`() =
        runTest {
            // Given
            val repository =
                FakeFavouriteRestaurantsRepository(
                    pagedResult = Result.Failure(DataError.Network(Exception("test error"))),
                )
            val useCase = GetFavouriteRestaurantsUseCaseImpl(repository)

            // When
            val result = useCase(page = 1, limit = 10)

            // Then
            assertIs<Result.Failure>(result)
        }
}
