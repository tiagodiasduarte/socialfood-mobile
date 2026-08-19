package pt.socialfood.domain.usecase.favourite.guide

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.fakes.FakeFavouritesGuidesRepository
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class IsGuideFavouriteUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns the result and forwards the guideId`() = runTest {
        // Given
        val guideId = Random.nextString()
        val repository = FakeFavouritesGuidesRepository(isFavouriteResult = Result.Success(true))
        val useCase = IsGuideFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(guideId)

        // Then
        assertEquals(Result.Success(true), result)
        assertEquals(guideId, repository.lastIsFavouriteGuideId)
    }

    @Test
    fun `given repository fails when invoked then returns Failure`() = runTest {
        // Given
        val repository = FakeFavouritesGuidesRepository(
            isFavouriteResult = Result.Failure(DataError.Network(Exception("test error"))),
        )
        val useCase = IsGuideFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(Random.nextString())

        // Then
        assertIs<Result.Failure>(result)
    }
}
