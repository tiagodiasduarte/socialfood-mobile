package pt.socialfood.presentation.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.socialfood.data.network.ConnectivityObserver
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.domain.usecase.favourite.SyncFavouriteRestaurantsUseCase
import pt.socialfood.domain.usecase.favourite.SyncFavouritesUseCase

class SyncViewModel(
    private val syncFavourites: SyncFavouritesUseCase,
    private val syncFavouriteRestaurants: SyncFavouriteRestaurantsUseCase,
    private val restaurantVisitStatusRepository: RestaurantVisitStatusRepository,
    connectivityObserver: ConnectivityObserver,
) : ViewModel() {

    init {
        viewModelScope.launch {
            var wasOnline: Boolean? = null
            connectivityObserver.isOnline.collect { isOnline ->
                if (wasOnline == false && isOnline) {
                    syncAll()
                }
                wasOnline = isOnline
            }
        }
    }

    fun onStart() {
        viewModelScope.launch { syncFavourites() }
        viewModelScope.launch { syncFavouriteRestaurants() }
        viewModelScope.launch { restaurantVisitStatusRepository.sync() }
    }

    private suspend fun syncAll() {
        syncFavourites()
        syncFavouriteRestaurants()
        restaurantVisitStatusRepository.sync()
    }
}
