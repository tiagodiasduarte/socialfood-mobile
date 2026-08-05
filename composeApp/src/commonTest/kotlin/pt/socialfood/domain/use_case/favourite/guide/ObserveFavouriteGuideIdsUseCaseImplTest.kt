package pt.socialfood.domain.use_case.favourite.guide

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.fakes.FakeFavouritesGuidesRepository
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertSame

class ObserveFavouriteGuideIdsUseCaseImplTest {
    @Test
    fun `given invoked then returns the repository's favourite guide ids flow`() = runTest {
        // Given
        val favouriteGuideIds = flowOf(setOf(Random.nextString(), Random.nextString()))
        val repository = FakeFavouritesGuidesRepository(favouriteGuideIds = favouriteGuideIds)
        val useCase = ObserveFavouriteGuideIdsUseCaseImpl(repository)

        // When
        val flow = useCase()

        // Then
        assertSame(favouriteGuideIds, flow)
    }
}
