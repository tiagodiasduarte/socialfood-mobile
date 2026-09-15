package pt.socialfood.presentation.map.restaurant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.usecase.restaurantvisitstatus.GetRestaurantVisitStatusListUseCase

class MapVisitRestaurantViewModel(
    getRestaurantVisitStatusList: GetRestaurantVisitStatusListUseCase,
    status: VisitStatus,
) : ViewModel() {

    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    init {
        viewModelScope.launch {
            getRestaurantVisitStatusList(status).collect { _restaurants.value = it }
        }
    }
}
