package pt.socialfood.domain.usecase.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Guide

interface GetJoinedGuidesPagingUseCase {
    operator fun invoke(userId: String): Flow<PagingData<Guide>>
}
