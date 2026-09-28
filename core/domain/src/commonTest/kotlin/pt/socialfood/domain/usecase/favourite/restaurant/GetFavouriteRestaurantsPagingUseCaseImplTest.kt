package pt.socialfood.domain.usecase.favourite.restaurant

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.fakes.FakeFavouriteRestaurantsRepository
import kotlin.test.Test
import kotlin.test.assertSame

class GetFavouriteRestaurantsPagingUseCaseImplTest {
    @Test
    fun `given invoked then returns the repository's paging flow`() = runTest {
        // Given
        val pagingFlow = flowOf(PagingData.empty<Restaurant>())
        val repository = FakeFavouriteRestaurantsRepository(pagingFlow = pagingFlow)
        val useCase = GetFavouriteRestaurantsPagingUseCaseImpl(repository)

        // When
        val flow = useCase()

        // Then
        assertSame(pagingFlow, flow)
    }
}
