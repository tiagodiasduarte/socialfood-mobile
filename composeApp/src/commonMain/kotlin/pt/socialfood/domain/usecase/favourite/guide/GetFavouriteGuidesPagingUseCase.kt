package pt.socialfood.domain.usecase.favourite.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Guide

interface GetFavouriteGuidesPagingUseCase {
    operator fun invoke(): Flow<PagingData<Guide>>
}
