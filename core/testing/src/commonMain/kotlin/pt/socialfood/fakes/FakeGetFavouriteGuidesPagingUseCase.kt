package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.Guide
import pt.socialfood.domain.usecase.favourite.guide.GetFavouriteGuidesPagingUseCase

class FakeGetFavouriteGuidesPagingUseCase(
    private val result: () -> Flow<PagingData<Guide>> = { flowOf(PagingData.empty()) },
) : GetFavouriteGuidesPagingUseCase {
    var invokeCount: Int = 0
        private set

    override operator fun invoke(): Flow<PagingData<Guide>> {
        invokeCount++
        return result()
    }
}
