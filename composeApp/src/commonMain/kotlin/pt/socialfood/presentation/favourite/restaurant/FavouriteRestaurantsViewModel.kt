package pt.socialfood.presentation.favourite.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.usecase.favourite.restaurant.GetFavouriteRestaurantsPagingUseCase
import pt.socialfood.domain.usecase.favourite.restaurant.UnmarkRestaurantFavouriteUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteRestaurantsViewModel(
    private val getFavouriteRestaurantsPaging: GetFavouriteRestaurantsPagingUseCase,
    private val unmarkRestaurantFavourite: UnmarkRestaurantFavouriteUseCase,
    observeUser: ObserveUserUseCase,
) : ViewModel() {

    val restaurants: Flow<PagingData<Restaurant>> = observeUser()
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { getFavouriteRestaurantsPaging() }
        .cachedIn(viewModelScope)

    fun removeFavourite(restaurantId: String) {
        viewModelScope.launch { unmarkRestaurantFavourite(restaurantId) }
    }
}
