package pt.socialfood.feature.map.restaurant.visitstatus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.core.model.Restaurant
import pt.socialfood.core.model.VisitStatus

class MapRestaurantVisitStatusViewModel(
    restaurantVisitStatusRepository: RestaurantVisitStatusRepository,
    status: VisitStatus,
) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    init {
        viewModelScope.launch {
            restaurantVisitStatusRepository.getAllFlow(status).collect { _restaurants.value = it }
        }
    }
}
