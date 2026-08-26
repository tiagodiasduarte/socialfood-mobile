package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.usecase.favourite.restaurant.GetFavouriteRestaurantsPagingUseCase

class FakeGetFavouriteRestaurantsPagingUseCase(
    private val result: () -> Flow<PagingData<Restaurant>> = { flowOf(PagingData.empty()) },
) : GetFavouriteRestaurantsPagingUseCase {
    var invokeCount: Int = 0
        private set

    override operator fun invoke(): Flow<PagingData<Restaurant>> {
        invokeCount++
        return result()
    }
}
