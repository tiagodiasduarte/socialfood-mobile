package pt.socialfood.presentation.favourite.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.usecase.favourite.restaurant.GetFavouriteRestaurantsPagingUseCase
import pt.socialfood.domain.usecase.favourite.restaurant.UnmarkRestaurantFavouriteUseCase

class FavouriteRestaurantsViewModel(
    private val getFavouriteRestaurantsPaging: GetFavouriteRestaurantsPagingUseCase,
    private val unmarkRestaurantFavourite: UnmarkRestaurantFavouriteUseCase,
) : ViewModel() {

    val restaurants: Flow<PagingData<Restaurant>> = getFavouriteRestaurantsPaging().cachedIn(viewModelScope)

    fun removeFavourite(restaurantId: String) {
        viewModelScope.launch { unmarkRestaurantFavourite(restaurantId) }
    }
}
