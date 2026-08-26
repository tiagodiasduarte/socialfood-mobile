package pt.socialfood.domain.usecase.favourite.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.FavouritesGuidesRepository

class GetFavouriteGuidesPagingUseCaseImpl(private val repository: FavouritesGuidesRepository) :
    GetFavouriteGuidesPagingUseCase {
    override operator fun invoke(): Flow<PagingData<Guide>> = repository.getFavouritesPagingFlow()
}
