package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.usecase.guide.GetGuidesPagingUseCase

class FakeGetGuidesPagingUseCase(
    private val result: (userId: String?) -> Flow<PagingData<Guide>> = { flowOf(PagingData.empty()) },
) : GetGuidesPagingUseCase {
    var invokeCount: Int = 0
        private set
    var lastUserId: String? = null
        private set

    override operator fun invoke(userId: String?): Flow<PagingData<Guide>> {
        invokeCount++
        lastUserId = userId
        return result(userId)
    }
}
