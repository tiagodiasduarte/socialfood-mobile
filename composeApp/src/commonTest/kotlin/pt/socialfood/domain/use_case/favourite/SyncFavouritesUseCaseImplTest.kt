package pt.socialfood.domain.use_case.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeFavouritesRepository
import kotlin.test.Test
import kotlin.test.assertIs

class SyncFavouritesUseCaseImplTest {

    @Test
    fun `given repository succeeds when invoked then returns Success`() = runTest {
        // Given
        val repository = FakeFavouritesRepository(syncResult = Result.Success(Unit))
        val useCase = SyncFavouritesUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Success<Unit>>(result)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeFavouritesRepository(syncResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = SyncFavouritesUseCaseImpl(repository)

        // When
        val result = useCase()

        // Then
        assertIs<Result.Failure>(result)
    }
}
