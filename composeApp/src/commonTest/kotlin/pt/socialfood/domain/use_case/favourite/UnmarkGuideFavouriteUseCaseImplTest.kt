package pt.socialfood.domain.use_case.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCaseImpl
import pt.socialfood.fakes.FakeFavouritesGuidesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class UnmarkGuideFavouriteUseCaseImplTest {

    @Test
    fun `given repository succeeds when invoked then delegates guideId and returns Success`() = runTest {
        // Given
        val repository = FakeFavouritesGuidesRepository(unmarkResult = Result.Success(Unit))
        val useCase = UnmarkGuideFavouriteUseCaseImpl(repository)

        // When
        val result = useCase("guide-id")

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals("guide-id", repository.lastUnmarkedGuideId)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeFavouritesGuidesRepository(unmarkResult = Result.Failure(DataError.Network(Exception("test error"))))
        val useCase = UnmarkGuideFavouriteUseCaseImpl(repository)

        // When
        val result = useCase("guide-id")

        // Then
        assertIs<Result.Failure>(result)
    }
}
