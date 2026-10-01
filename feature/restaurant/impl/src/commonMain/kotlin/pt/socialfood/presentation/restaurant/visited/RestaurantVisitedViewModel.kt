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
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.domain.usecase.user.ObserveUserUseCase
import pt.socialfood.presentation.restaurant.navigation.RestaurantPickerResults

private val STATUS = VisitStatus.VISITED

@OptIn(ExperimentalCoroutinesApi::class)
class RestaurantVisitedViewModel(
    private val restaurantVisitStatusRepository: RestaurantVisitStatusRepository,
    observeUser: ObserveUserUseCase,
    restaurantPickerResults: RestaurantPickerResults,
) : ViewModel() {

    /** Request key for the restaurant picker opened from this screen. */
    val restaurantPickerKey = "restaurant-visited"

    val restaurants: Flow<PagingData<Restaurant>> = observeUser()
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { restaurantVisitStatusRepository.getPagingFlow(STATUS) }
        .map { pagingData -> pagingData.map { it.restaurant } }
        .cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            restaurantPickerResults.results(restaurantPickerKey).collect(::addToVisited)
        }
    }

    private fun addToVisited(restaurant: Restaurant) {
        viewModelScope.launch { restaurantVisitStatusRepository.mark(restaurant, STATUS) }
    }

    fun removeFromVisited(restaurantId: String) {
        viewModelScope.launch { restaurantVisitStatusRepository.unmark(restaurantId, STATUS) }
    }
}
