package pt.socialfood.domain.usecase.favourite.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.Guide
import pt.socialfood.fakes.FakeFavouritesGuidesRepository
import kotlin.test.Test
import kotlin.test.assertSame

class GetFavouriteGuidesPagingUseCaseImplTest {
    @Test
    fun `given invoked then returns the repository's paging flow`() = runTest {
        // Given
        val pagingFlow = flowOf(PagingData.empty<Guide>())
        val repository = FakeFavouritesGuidesRepository(pagingFlow = pagingFlow)
        val useCase = GetFavouriteGuidesPagingUseCaseImpl(repository)

        // When
        val flow = useCase()

        // Then
        assertSame(pagingFlow, flow)
    }
}
