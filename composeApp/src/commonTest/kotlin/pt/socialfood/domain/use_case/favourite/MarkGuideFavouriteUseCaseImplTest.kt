package pt.socialfood.domain.use_case.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.ApiError
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.GuideVisibility
import pt.socialfood.domain.use_case.favourite.guide.MarkGuideFavouriteUseCaseImpl
import pt.socialfood.fakes.FakeFavouritesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MarkGuideFavouriteUseCaseImplTest {

    private val fakeGuide = Guide(
        id = "guide-id",
        name = "Guide Name",
        description = "Guide Description",
        visibility = GuideVisibility.PUBLIC,
        author = Author(id = "author-id", name = "Author Name", username = "authorname"),
        numberOfRestaurant = 0,
    )

    @Test
    fun `given repository succeeds when invoked then delegates guide and returns Success`() = runTest {
        // Given
        val repository = FakeFavouritesRepository(markResult = Result.Success(Unit))
        val useCase = MarkGuideFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(fakeGuide)

        // Then
        assertIs<Result.Success<Unit>>(result)
        assertEquals(fakeGuide, repository.lastMarkedGuide)
    }

    @Test
    fun `given repository fails when invoked then returns Error`() = runTest {
        // Given
        val repository =
            FakeFavouritesRepository(markResult = Result.Failure(ApiError.Network(Exception("test error"))))
        val useCase = MarkGuideFavouriteUseCaseImpl(repository)

        // When
        val result = useCase(fakeGuide)

        // Then
        assertIs<Result.Failure>(result)
    }
}
