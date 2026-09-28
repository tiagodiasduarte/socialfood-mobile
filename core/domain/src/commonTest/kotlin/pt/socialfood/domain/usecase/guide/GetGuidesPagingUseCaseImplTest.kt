package pt.socialfood.domain.usecase.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.Guide
import pt.socialfood.fakes.FakeGuidesRepository
import kotlin.test.Test
import kotlin.test.assertSame

class GetGuidesPagingUseCaseImplTest {
    @Test
    fun `given the use case is invoked then returns the repository's all guides paging flow`() = runTest {
        // Given
        val pagingFlow = flowOf(PagingData.empty<Guide>())
        val repository = FakeGuidesRepository(guidesPagingFlow = pagingFlow)
        val useCase = GetGuidesPagingUseCaseImpl(repository)

        // When
        val flow = useCase()

        // Then
        assertSame(pagingFlow, flow)
    }
}
