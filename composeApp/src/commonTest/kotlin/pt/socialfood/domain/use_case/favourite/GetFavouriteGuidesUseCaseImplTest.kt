package pt.socialfood.domain.use_case.favourite

import kotlinx.coroutines.test.runTest
import pt.socialfood.core.Result
import pt.socialfood.domain.error.DataError
import pt.socialfood.domain.model.PagedFavouriteGuides
import pt.socialfood.domain.use_case.favourite.guide.GetFavouriteGuidesUseCaseImpl
import pt.socialfood.fakes.FakeFavouritesRepository
import kotlin.test.Test
import kotlin.test.assertIs

class GetFavouriteGuidesUseCaseImplTest {
    @Test
    fun `given repository succeeds when invoked then returns Success with PagedFavouriteGuides`() =
        runTest {
            // Given
            val repository =
                FakeFavouritesRepository(
                    pagedResult =
                        Result.Success(
                            PagedFavouriteGuides(favourites = emptyList(), page = 1, total = 0, hasMore = false),
                        ),
                )
            val useCase = GetFavouriteGuidesUseCaseImpl(repository)

            // When
            val result = useCase(page = 1, limit = 10)

            // Then
            assertIs<Result.Success<PagedFavouriteGuides>>(result)
        }

    @Test
    fun `given repository fails when invoked then returns Error`() =
        runTest {
            // Given
            val repository =
                FakeFavouritesRepository(pagedResult = Result.Failure(DataError.Network(Exception("test error"))))
            val useCase = GetFavouriteGuidesUseCaseImpl(repository)

            // When
            val result = useCase(page = 1, limit = 10)

            // Then
            assertIs<Result.Failure>(result)
        }
}
