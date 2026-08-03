package pt.socialfood.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pt.socialfood.core.Result
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.model.HomeSection
import pt.socialfood.domain.model.Restaurant
import pt.socialfood.domain.use_case.favourite.guide.IsGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.MarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.guide.UnmarkGuideFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.IsRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.MarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.favourite.restaurant.UnmarkRestaurantFavouriteUseCase
import pt.socialfood.domain.use_case.home.GetHomeSectionsUseCase
import pt.socialfood.domain.use_case.home.ObserveHomeSectionsUseCase
import pt.socialfood.presentation.error.displayMessage

class HomeViewModel(
    private val getHomeSections: GetHomeSectionsUseCase,
    private val isRestaurantFavourite: IsRestaurantFavouriteUseCase,
    private val markRestaurantFavourite: MarkRestaurantFavouriteUseCase,
    private val unmarkRestaurantFavourite: UnmarkRestaurantFavouriteUseCase,
    private val isGuideFavourite: IsGuideFavouriteUseCase,
    private val markGuideFavourite: MarkGuideFavouriteUseCase,
    private val unmarkGuideFavourite: UnmarkGuideFavouriteUseCase,
    observeHomeSections: ObserveHomeSectionsUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    val sections: StateFlow<List<HomeSection>> =
        observeHomeSections()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = HomeUiState.Loading
            fetchSections()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchSections()
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchSections() {
        when (val result = getHomeSections()) {
            is Result.Success -> {
                val active =
                    result.data
                        .filter { it.isActive }
                        .sortedBy { it.position }
                val items = active.flatMap { it.items }

                val (favouriteRestaurantIds, favouriteGuideIds) =
                    coroutineScope {
                        val restaurantIds = items.mapNotNull { it.restaurant?.id }.distinct()
                        val guideIds = items.mapNotNull { it.guide?.id }.distinct()

                        val restaurantsDeferred = restaurantIds.map { id -> async { id to isRestaurantFavourite(id) } }
                        val guidesDeferred = guideIds.map { id -> async { id to isGuideFavourite(id) } }

                        val favouriteRestaurants =
                            restaurantsDeferred
                                .awaitAll()
                                .filter { (_, result) -> (result as? Result.Success)?.data == true }
                                .map { it.first }
                                .toSet()
                        val favouriteGuides =
                            guidesDeferred
                                .awaitAll()
                                .filter { (_, result) -> (result as? Result.Success)?.data == true }
                                .map { it.first }
                                .toSet()

                        favouriteRestaurants to favouriteGuides
                    }

                _state.value =
                    HomeUiState.Loaded(
                        favouriteRestaurantIds = favouriteRestaurantIds,
                        favouriteGuideIds = favouriteGuideIds,
                    )
            }
            is Result.Failure -> _state.value = HomeUiState.Error(result.error.displayMessage())
        }
    }

    fun onToggleRestaurantFavourite(restaurant: Restaurant) {
        val current = _state.value as? HomeUiState.Loaded ?: return
        val isFavourite = restaurant.id in current.favouriteRestaurantIds
        val newIds =
            if (isFavourite) {
                current.favouriteRestaurantIds - restaurant.id
            } else {
                current.favouriteRestaurantIds + restaurant.id
            }
        _state.value = current.copy(favouriteRestaurantIds = newIds)

        viewModelScope.launch {
            val result =
                if (isFavourite) {
                    unmarkRestaurantFavourite(restaurant.id)
                } else {
                    markRestaurantFavourite(restaurant)
                }
            if (result is Result.Failure) {
                val stateNow = _state.value as? HomeUiState.Loaded ?: return@launch
                val revertedIds =
                    if (isFavourite) {
                        stateNow.favouriteRestaurantIds + restaurant.id
                    } else {
                        stateNow.favouriteRestaurantIds - restaurant.id
                    }
                _state.value = stateNow.copy(favouriteRestaurantIds = revertedIds)
            }
        }
    }

    fun onToggleGuideFavourite(guide: Guide) {
        val current = _state.value as? HomeUiState.Loaded ?: return
        val isFavourite = guide.id in current.favouriteGuideIds
        val newIds = if (isFavourite) current.favouriteGuideIds - guide.id else current.favouriteGuideIds + guide.id
        _state.value = current.copy(favouriteGuideIds = newIds)

        viewModelScope.launch {
            val result =
                if (isFavourite) {
                    unmarkGuideFavourite(guide.id)
                } else {
                    markGuideFavourite(guide)
                }
            if (result is Result.Failure) {
                val stateNow = _state.value as? HomeUiState.Loaded ?: return@launch
                val revertedIds =
                    if (isFavourite) {
                        stateNow.favouriteGuideIds + guide.id
                    } else {
                        stateNow.favouriteGuideIds - guide.id
                    }
                _state.value = stateNow.copy(favouriteGuideIds = revertedIds)
            }
        }
    }
}
