package pt.socialfood.domain.usecase.wishlist

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.PagedWishlistRestaurants
import pt.socialfood.domain.usecase.wishlist.restaurant.GetWishlistRestaurantsUseCaseImpl
import pt.socialfood.fakes.FakeWishlistRestaurantsRepository
import kotlin.test.Test
import kotlin.test.assertIs

class GetWishlistRestaurantsUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success with PagedWishlistRestaurants`() = runTest {
        // Given
        val repository = FakeWishlistRestaurantsRepository(
            pagedResult = Result.Success(
                PagedWishlistRestaurants(wishlist = emptyList(), page = 1, total = 0, hasMore = false),
            ),
        )
        val useCase = GetWishlistRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase(page = 1, limit = 10)

        // Then
        assertIs<Result.Success<PagedWishlistRestaurants>>(result)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository = FakeWishlistRestaurantsRepository(
            pagedResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = GetWishlistRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase(page = 1, limit = 10)

        // Then
        assertIs<Result.Failure>(result)
    }
}
