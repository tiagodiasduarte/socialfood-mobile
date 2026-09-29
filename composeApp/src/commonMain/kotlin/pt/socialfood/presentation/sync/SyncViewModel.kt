package pt.socialfood.presentation.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pt.socialfood.data.network.ConnectivityObserver
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.FavouritesGuidesRepository
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository

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
