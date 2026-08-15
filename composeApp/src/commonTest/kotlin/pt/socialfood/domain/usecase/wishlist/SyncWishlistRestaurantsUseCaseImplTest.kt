package pt.socialfood.domain.usecase.wishlist

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeWishlistRestaurantsRepository
import kotlin.test.Test
import kotlin.test.assertIs

class SyncWishlistRestaurantsUseCaseImplTest {

    @Test
    fun `given repository succeeds when invoked then returns Success`() = runTest {
        // Given
        val repository = FakeWishlistRestaurantsRepository(syncResult = Result.Success(Unit))
        val useCase = SyncWishlistRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository = FakeWishlistRestaurantsRepository(
            syncResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = SyncWishlistRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
