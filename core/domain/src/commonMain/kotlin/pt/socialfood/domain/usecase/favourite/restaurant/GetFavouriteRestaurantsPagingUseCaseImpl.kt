package pt.socialfood.domain.usecase.favourite.restaurant

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository

class GetFavouriteRestaurantsPagingUseCaseImpl(private val repository: FavouriteRestaurantsRepository) :
    GetFavouriteRestaurantsPagingUseCase {
    override operator fun invoke(): Flow<PagingData<Restaurant>> = repository.getFavouritesPagingFlow()
}
