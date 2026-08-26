package pt.socialfood.presentation.favourite.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.usecase.favourite.guide.GetFavouriteGuidesPagingUseCase
import pt.socialfood.domain.usecase.favourite.guide.UnmarkGuideFavouriteUseCase

class FavouriteGuidesViewModel(
    private val getFavouriteGuidesPaging: GetFavouriteGuidesPagingUseCase,
    private val unmarkGuideFavourite: UnmarkGuideFavouriteUseCase,
) : ViewModel() {

    val guides: Flow<PagingData<Guide>> = getFavouriteGuidesPaging().cachedIn(viewModelScope)

    fun removeFavourite(guideId: String) {
        viewModelScope.launch { unmarkGuideFavourite(guideId) }
    }
}
