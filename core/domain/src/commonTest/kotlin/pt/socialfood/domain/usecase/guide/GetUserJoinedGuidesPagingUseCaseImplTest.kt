package pt.socialfood.domain.usecase.guide

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.Guide
import pt.socialfood.fakes.FakeGuidesRepository
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetUserJoinedGuidesPagingUseCaseImplTest {
    @Test
    fun `given a userId when invoked then forwards it and returns the repository's joined guides paging flow`() =
        runTest {
            // Given
            val userId = Random.nextString()
            val pagingFlow = flowOf(PagingData.empty<Guide>())
            val repository = FakeGuidesRepository(joinedGuidesPagingFlow = pagingFlow)
            val useCase = GetUserJoinedGuidesPagingUseCaseImpl(repository)

            // When
            val flow = useCase(userId)

            // Then
            assertSame(pagingFlow, flow)
            assertEquals(userId, repository.lastJoinedPagingUserId)
        }
}
