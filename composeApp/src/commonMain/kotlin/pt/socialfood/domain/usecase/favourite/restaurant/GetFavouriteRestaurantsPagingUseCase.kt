package pt.socialfood.domain.usecase.favourite.restaurant

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Restaurant

interface GetFavouriteRestaurantsPagingUseCase {
    operator fun invoke(): Flow<PagingData<Restaurant>>
}
