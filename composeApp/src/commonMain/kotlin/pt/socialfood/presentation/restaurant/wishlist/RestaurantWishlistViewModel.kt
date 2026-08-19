package pt.socialfood.presentation.restaurant.wishlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetRestaurantVisitStatusPagingUseCase
import pt.socialfood.domain.usecase.restaurantvisitstatus.MarkRestaurantVisitStatusUseCase
import pt.socialfood.domain.usecase.restaurantvisitstatus.UnmarkRestaurantVisitStatusUseCase

private val STATUS = VisitStatus.WISHLIST

class RestaurantWishlistViewModel(
    private val getRestaurantVisitStatusPaging: GetRestaurantVisitStatusPagingUseCase,
    private val markRestaurantVisitStatus: MarkRestaurantVisitStatusUseCase,
    private val unmarkRestaurantVisitStatus: UnmarkRestaurantVisitStatusUseCase,
) : ViewModel() {

    val restaurants: Flow<PagingData<Restaurant>> = getRestaurantVisitStatusPaging(STATUS)
        .map { pagingData -> pagingData.map { it.restaurant } }
        .cachedIn(viewModelScope)

    fun addToWishlist(restaurant: Restaurant) {
        viewModelScope.launch { markRestaurantVisitStatus(restaurant, STATUS) }
    }

    fun removeFromWishlist(restaurantId: String) {
        viewModelScope.launch { unmarkRestaurantVisitStatus(restaurantId, STATUS) }
    }
}
