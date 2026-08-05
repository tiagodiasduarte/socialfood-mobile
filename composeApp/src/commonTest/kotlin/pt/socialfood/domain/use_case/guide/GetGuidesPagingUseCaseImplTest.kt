package pt.socialfood.domain.use_case.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.Guide
import pt.socialfood.fakes.FakeGuidesRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetGuidesPagingUseCaseImplTest {
    @Test
    fun `given a userId when invoked then forwards it and returns the repository's paging flow`() = runTest {
        // Given
        val pagingFlow = flowOf(PagingData.empty<Guide>())
        val repository = FakeGuidesRepository(guidesPagingFlow = pagingFlow)
        val useCase = GetGuidesPagingUseCaseImpl(repository)

        // When
        val flow = useCase("author-id")

        // Then
        assertSame(pagingFlow, flow)
        assertEquals("author-id", repository.lastPagingUserId)
    }

    @Test
    fun `given no userId when invoked then forwards null`() = runTest {
        // Given
        val repository = FakeGuidesRepository()
        val useCase = GetGuidesPagingUseCaseImpl(repository)

        // When
        useCase(null)

        // Then
        assertEquals(null, repository.lastPagingUserId)
    }
}
