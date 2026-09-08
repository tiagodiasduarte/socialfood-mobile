package pt.socialfood.domain.usecase.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.repository.GuidesRepository

class GetUserGuidesPagingUseCaseImpl(private val repository: GuidesRepository) : GetUserGuidesPagingUseCase {
    override operator fun invoke(userId: String): Flow<PagingData<Guide>> = repository.findUserGuidesPagingFlow(userId)
}
