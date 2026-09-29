package pt.socialfood.presentation.favourite.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.FavouritesGuidesRepository
import pt.socialfood.domain.usecase.user.ObserveUserUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteGuidesViewModel(
    private val favouritesGuidesRepository: FavouritesGuidesRepository,
    observeUser: ObserveUserUseCase,
) : ViewModel() {

    val guides: Flow<PagingData<Guide>> = observeUser()
        .filterNotNull()
        .map { it.id }
        .distinctUntilChanged()
        .flatMapLatest { favouritesGuidesRepository.getFavouritesPagingFlow() }
        .cachedIn(viewModelScope)

    fun removeFavourite(guideId: String) {
        viewModelScope.launch { favouritesGuidesRepository.unmark(guideId) }
    }
}
