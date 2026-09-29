package pt.socialfood.presentation.restaurant.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.VisitStatus
import pt.socialfood.domain.repository.FavouriteRestaurantsRepository
import pt.socialfood.domain.repository.RestaurantVisitStatusRepository
import pt.socialfood.domain.repository.RestaurantsRepository
import pt.socialfood.presentation.error.toErrorCode

class RestaurantDetailViewModel(
    private val restaurantsRepository: RestaurantsRepository,
    private val favouriteRestaurantsRepository: FavouriteRestaurantsRepository,
    private val restaurantVisitStatusRepository: RestaurantVisitStatusRepository,
    private val restaurantId: String,
) : ViewModel() {

    private val _state = MutableStateFlow<RestaurantDetailUiState>(RestaurantDetailUiState.Loading)
    val state: StateFlow<RestaurantDetailUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = RestaurantDetailUiState.Loading
            val restaurantDeferred = async { restaurantsRepository.findById(restaurantId) }
            val isFavouriteDeferred = async { favouriteRestaurantsRepository.isFavourite(restaurantId) }
            val visitStatusDeferred = async { restaurantVisitStatusRepository.getStatus(restaurantId) }
            val restaurantResult = restaurantDeferred.await()
            val isFavouriteResult = isFavouriteDeferred.await()
            val visitStatusResult = visitStatusDeferred.await()
            _state.value = when (restaurantResult) {
                is Result.Success -> RestaurantDetailUiState.Loaded(
                    restaurant = restaurantResult.data,
                    isFavourite = (isFavouriteResult as? Result.Success)?.data ?: false,
                    visitStatus = (visitStatusResult as? Result.Success)?.data,
                )
                is Result.Failure -> RestaurantDetailUiState.Error(restaurantResult.error.toErrorCode())
            }
        }
    }

    fun toggleFavourite() {
        val current = _state.value as? RestaurantDetailUiState.Loaded ?: return
        val newIsFavourite = !current.isFavourite
        _state.value = current.copy(isFavourite = newIsFavourite)

        viewModelScope.launch {
            val result = if (newIsFavourite) {
                favouriteRestaurantsRepository.markFavourite(current.restaurant)
            } else {
                favouriteRestaurantsRepository.unmarkFavourite(current.restaurant.id)
            }
            if (result is Result.Failure) {
                val stateNow = _state.value as? RestaurantDetailUiState.Loaded ?: return@launch
                _state.value = stateNow.copy(isFavourite = !newIsFavourite)
            }
        }
    }

    fun addToWishlist() = markVisitStatus(VisitStatus.WISHLIST)

    fun moveToVisited() = markVisitStatus(VisitStatus.VISITED)

    private fun markVisitStatus(status: VisitStatus) {
        val current = _state.value as? RestaurantDetailUiState.Loaded ?: return
        val previousStatus = current.visitStatus
        _state.value = current.copy(visitStatus = status)

        viewModelScope.launch {
            val result = restaurantVisitStatusRepository.mark(current.restaurant, status)
            if (result is Result.Failure) {
                val stateNow = _state.value as? RestaurantDetailUiState.Loaded ?: return@launch
                _state.value = stateNow.copy(visitStatus = previousStatus)
            }
        }
    }
}
