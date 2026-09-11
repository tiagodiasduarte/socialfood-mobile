package pt.socialfood.presentation.restaurant.visited

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetRestaurantVisitStatusPagingUseCase
import pt.socialfood.domain.usecase.restaurantvisitstatus.UnmarkRestaurantVisitStatusUseCase
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

private val STATUS = VisitStatus.VISITED

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantVisitedViewModel(
    private val getRestaurantVisitStatusPaging: GetRestaurantVisitStatusPagingUseCase,
    private val unmarkRestaurantVisitStatus: UnmarkRestaurantVisitStatusUseCase,
    observeUser: ObserveUserUseCase,
) : ViewModel() {

    // Re-creates the Pager whenever the current user changes, so a logout+login as a different
    // account doesn't keep showing the previous account's cached-then-cleared data.
    val restaurants: Flow<PagingData<Restaurant>> = observeUser()
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { getRestaurantVisitStatusPaging(STATUS) }
        .map { pagingData -> pagingData.map { it.restaurant } }
        .cachedIn(viewModelScope)

    fun removeFromVisited(restaurantId: String) {
        viewModelScope.launch { unmarkRestaurantVisitStatus(restaurantId, STATUS) }
    }
}
