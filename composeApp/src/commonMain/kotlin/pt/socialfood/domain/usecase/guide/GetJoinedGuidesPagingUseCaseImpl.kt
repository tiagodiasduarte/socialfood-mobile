package pt.socialfood.domain.usecase.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.GuidesRepository

class GetJoinedGuidesPagingUseCaseImpl(private val repository: GuidesRepository) : GetJoinedGuidesPagingUseCase {
    override operator fun invoke(userId: String): Flow<PagingData<Guide>> =
        repository.findUserGuidesJoinedPagingFlow(userId)
}
