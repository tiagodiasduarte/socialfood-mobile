package pt.socialfood.domain.use_case.author

import androidx.paging.PagingData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import pt.socialfood.domain.model.Author
import pt.socialfood.fakes.FakeAuthorsRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class GetAuthorsPagingUseCaseImplTest {
    @Test
    fun `given invoked then returns the repository's paging flow`() = runTest {
        // Given
        val pagingFlow = flowOf(PagingData.empty<Author>())
        val repository = FakeAuthorsRepository(authorsPagingFlow = pagingFlow)
        val useCase = GetAuthorsPagingUseCaseImpl(repository)

        // When
        val flow = useCase()

        // Then
        assertSame(pagingFlow, flow)
        assertEquals(1, repository.getAuthorsPagingFlowInvokeCount)
    }
}
