package pt.socialfood.domain.usecase.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Guide

interface GetGuidesPagingUseCase {
    operator fun invoke(userId: String? = null): Flow<PagingData<Guide>>
}
