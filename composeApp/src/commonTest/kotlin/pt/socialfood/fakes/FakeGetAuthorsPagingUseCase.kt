package pt.socialfood.fakes

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import pt.socialfood.domain.model.Author
import pt.socialfood.domain.use_case.author.GetAuthorsPagingUseCase

class FakeGetAuthorsPagingUseCase(
    private val result: () -> Flow<PagingData<Author>> = { flowOf(PagingData.empty()) },
) : GetAuthorsPagingUseCase {
    var invokeCount: Int = 0
        private set

    override operator fun invoke(): Flow<PagingData<Author>> {
        invokeCount++
        return result()
    }
}
