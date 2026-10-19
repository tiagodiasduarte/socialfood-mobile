package pt.socialfood.app.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.socialfood.core.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.core.domain.repository.FavouritesGuidesRepository
import pt.socialfood.core.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.core.network.ConnectivityObserver

class SyncViewModel(
    private val favouritesGuidesRepository: FavouritesGuidesRepository,
    private val favouriteRestaurantsRepository: FavouriteRestaurantsRepository,
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
        viewModelScope.launch { favouritesGuidesRepository.sync() }
        viewModelScope.launch { favouriteRestaurantsRepository.syncFavourites() }
        viewModelScope.launch { restaurantVisitStatusRepository.sync() }
    }

    private suspend fun syncAll() {
        favouritesGuidesRepository.sync()
        favouriteRestaurantsRepository.syncFavourites()
        restaurantVisitStatusRepository.sync()
    }
}
