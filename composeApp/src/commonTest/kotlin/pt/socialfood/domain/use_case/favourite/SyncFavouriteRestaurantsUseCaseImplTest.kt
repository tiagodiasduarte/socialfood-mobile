package pt.socialfood.domain.use_case.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ApiError
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import kotlin.test.Test
import kotlin.test.assertIs

class SyncFavouriteRestaurantsUseCaseImplTest {

    @Test
    fun `given repository succeeds when invoked then returns Success`() = runTest {
        // Given
        val repository = FakeFavouriteRestaurantsRepository(syncResult = Result.Success(Unit))
        val useCase = SyncFavouriteRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeFavouriteRestaurantsRepository(syncResult = Result.Failure(ApiError.Network(Exception("test error"))))
        val useCase = SyncFavouriteRestaurantsUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
